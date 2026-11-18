package com.demo.dto;


public record DicomMetaData(
        String patientIdentifier,
        String modality,
        String studyUid,
        String sopInstanceUid) {
}
