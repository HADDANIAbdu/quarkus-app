package com.demo.resource;

import com.demo.dto.DicomMetaData;
import com.demo.service.DicomService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/dicom")
public class DicomResource {

    private final DicomService service;

    public DicomResource(DicomService service) {
        this.service = service;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DicomMetaData receive(@RestForm("file") FileUpload file) {
        if (file == null || file.size() == 0) {
            throw new BadRequestException(
                    "Ajoute un fichier DICOM dans le champ 'file'.");
        }

        return service.process(file.uploadedFile());
    }
}