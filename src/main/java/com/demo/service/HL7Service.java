package com.demo.service;


import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import com.demo.entity.Hl7Message;
import com.demo.entity.Patient;
import com.demo.repository.Hl7MessageRepo;
import com.demo.repository.PatientRepo;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;

import java.io.IOException;
import java.time.Instant;


@ApplicationScoped
public class HL7Service {
    private final PatientRepo patientRepo;
    private final Hl7MessageRepo  hl7MessageRepo;
    private record PatientData(
            String identifier,
            String firstName,
            String lastName
    ){}

    public HL7Service(PatientRepo patientRepo, Hl7MessageRepo hl7MessageRepo) {
        this.patientRepo = patientRepo;
        this.hl7MessageRepo = hl7MessageRepo;
    }

    public String processMessage(String rawMessage) {
        Message message = parseMessage(rawMessage);
        PipeParser parser = new PipeParser();

        PatientData data;

        try {
            data = extractPatientData(message);
        } catch (HL7Exception e) {
            return encodeAck(parser, message, AcknowledgmentCode.AE, e);
        }

        String acknowledgment = encodeAck(
                parser, message, AcknowledgmentCode.AA, null);

        saveMessageAndPatient(rawMessage, data);

        return acknowledgment;
    }

    private Message parseMessage(String rawMessage){
        if(rawMessage == null || rawMessage.isBlank()){
            throw new BadRequestException("Empty or blank Hl7 Message");
        }

        String normalizedMessage = rawMessage
                .replace("\r\n", "\r")
                .replace("\n", "\r");
        System.out.println("-----------"+ normalizedMessage);
        PipeParser pipeParser = new PipeParser();

        Message message;
        try {
            message = pipeParser.parse(normalizedMessage);
        } catch (HL7Exception e){
            throw new BadRequestException("Le message HL7 est invalide.", e);
        }
        return message;
    }

    private PatientData extractPatientData(Message message) throws HL7Exception {
        if (!(message instanceof ADT_A01 adt)
                || !"2.5.1".equals(message.getVersion())) {
            throw new HL7Exception(
                    "Un message ADT_A01 en version 2.5.1 est attendu.");
        }

        PID pid = adt.getPID();

        String identifier = required(
                pid.getPatientIdentifierList(0).getIDNumber().getValue(),
                "Identifiant patient", 64);

        String firstName = required(
                pid.getPatientName(0).getGivenName().getValue(),
                "Prénom", 100);

        String lastName = required(
                pid.getPatientName(0).getFamilyName().getSurname().getValue(),
                "Nom", 100);

        return new PatientData(identifier, firstName, lastName);
    }

    private String required(String value, String field, int maxLength)
            throws HL7Exception {
        if (value == null || value.isBlank()) {
            throw new HL7Exception(field + " obligatoire.");
        }

        String cleanedValue = value.strip();

        if (cleanedValue.length() > maxLength) {
            throw new HL7Exception(
                    field + " dépasse " + maxLength + " caractères.");
        }

        return cleanedValue;
    }
    private void saveMessageAndPatient(String rawMessage, PatientData data) {
        QuarkusTransaction.requiringNew().run(() -> {
            Patient patient = patientRepo
                    .find("patientId", data.identifier())
                    .firstResult();

            if (patient == null) {
                patient = new Patient();
                patient.patientId = data.identifier();
                patient.firstName = data.firstName();
                patient.lastName = data.lastName();
                patientRepo.persist(patient);
            } else {
                patient.firstName = data.firstName();
                patient.lastName = data.lastName();
            }

            Hl7Message storedMessage = new Hl7Message();
            storedMessage.patient = patient;
            storedMessage.rawMessage = rawMessage;
            storedMessage.receivedAt = Instant.now();

            hl7MessageRepo.persist(storedMessage);
        });
    }

    private String encodeAck(
            PipeParser parser,
            Message message,
            AcknowledgmentCode code,
            HL7Exception error) {
        try {
            Message acknowledgment = message.generateACK(code, error);
            return parser.encode(acknowledgment);
        } catch (HL7Exception | IOException e) {
            throw new InternalServerErrorException(
                    "Impossible de générer l'ACK.", e);
        }
    }

}
