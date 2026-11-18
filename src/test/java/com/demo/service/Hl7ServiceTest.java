package com.demo.service;

import com.demo.repository.Hl7MessageRepo;
import com.demo.repository.PatientRepo;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class Hl7ServiceTest {

    @Mock
    PatientRepo patientRepo;

    @Mock
    Hl7MessageRepo messageRepository;

    @InjectMocks
    HL7Service service;

    @Test
    void shouldRejectEmptyMessage() {
        assertThrows(
                BadRequestException.class,
                () -> service.processMessage("   ")
        );

        verifyNoInteractions(patientRepo, messageRepository);
    }

    @Test
    void shouldRejectUnreadableMessage() {
        assertThrows(
                BadRequestException.class,
                () -> service.processMessage("bonjour")
        );

        verifyNoInteractions(patientRepo, messageRepository);
    }

    @Test
    void shouldReturnErrorAckWhenFirstNameIsMissing() {
        String raw =
                "MSH|^~\\&|APP|HOP|DEST|HOP|20261118003000"
                        + "||ADT^A01^ADT_A01|MSG001|P|2.5.1\r"
                        + "EVN|A01|20261118003000\r"
                        + "PID|1||12345^^^HOP||DUPONT\r"
                        + "PV1|1|I\r";

        String response = service.processMessage(raw);

        assertTrue(
                response.contains("MSA|AE|MSG001"),
                "Un ACK d'erreur lié à MSG001 est attendu."
        );

        verifyNoInteractions(patientRepo, messageRepository);
    }
}
