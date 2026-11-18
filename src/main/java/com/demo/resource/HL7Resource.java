package com.demo.resource;


import com.demo.service.HL7Service;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hl7")
public class HL7Resource {
    private final HL7Service  hl7Service;

    public HL7Resource(HL7Service hl7Service) {
        this.hl7Service = hl7Service;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String receive(String rawMessage) {
        return hl7Service.processMessage(rawMessage);
    }
}
