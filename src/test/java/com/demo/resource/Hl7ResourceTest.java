package com.demo.resource;


import com.demo.service.HL7Service;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class Hl7ResourceTest {

    @InjectMock
    HL7Service service;

    @Test
    void shouldReturnAckFromService() {
        String raw = "message transmis au service";
        String ack = "MSA|AA|MSG001\r";

        when(service.processMessage(raw)).thenReturn(ack);

        given()
                .contentType("text/plain")
                .body(raw)
                .when()
                .post("/hl7")
                .then()
                .statusCode(200)
                .contentType("text/plain")
                .body(equalTo(ack));

        verify(service).processMessage(raw);
    }

    @Test
    void shouldReturn400WhenServiceRejectsMessage() {
        String raw = "message invalide";

        when(service.processMessage(raw))
                .thenThrow(new BadRequestException("Message invalide."));

        given()
                .contentType("text/plain")
                .body(raw)
                .when()
                .post("/hl7")
                .then()
                .statusCode(400);

        verify(service).processMessage(raw);
    }
}
