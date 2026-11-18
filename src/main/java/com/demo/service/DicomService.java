package com.demo.service;

import com.demo.dto.DicomMetaData;
import com.demo.repository.DicomInstanceRepo;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import java.io.IOException;
import java.nio.file.Path;

@ApplicationScoped
public class DicomService {

    private final DicomInstanceRepo repository;

    public DicomService(DicomInstanceRepo repository) {
        this.repository = repository;
    }

    public DicomMetaData process(Path file) {
        DicomMetaData metadata = extractMetadata(file);

        QuarkusTransaction.requiringNew().run(
                () -> repository.saveMetadata(metadata));

        return metadata;
    }

    private DicomMetaData extractMetadata(Path file) {
        try (DicomInputStream input =
                     new DicomInputStream(file.toFile())) {

            input.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);

            Attributes attributes = input.readDataset();

            return new DicomMetaData(
                    required(attributes, Tag.PatientID, "PatientID", 64),
                    required(attributes, Tag.Modality, "Modality", 16),
                    required(attributes, Tag.StudyInstanceUID,
                            "StudyInstanceUID", 64),
                    required(attributes, Tag.SOPInstanceUID,
                            "SOPInstanceUID", 64));

        } catch (IOException e) {
            throw new BadRequestException(
                    "Impossible de lire le fichier DICOM.", e);
        }
    }

    private String required(
            Attributes attributes,
            int tag,
            String field,
            int maxLength) {

        String value = attributes.getString(tag);

        if (value == null || value.isBlank()) {
            throw new BadRequestException(
                    "Champ DICOM obligatoire pour cet import : " + field);
        }

        String cleanedValue = value.strip();

        if (cleanedValue.length() > maxLength) {
            throw new BadRequestException(
                    field + " dépasse " + maxLength + " caractères.");
        }

        return cleanedValue;
    }
}
