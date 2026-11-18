package com.demo.repository;

import com.demo.dto.DicomMetaData;
import com.demo.entity.DicomInstance;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DicomInstanceRepo
        implements PanacheRepository<DicomInstance> {

    public void saveMetadata(DicomMetaData data) {
        getEntityManager().createNativeQuery("""
                INSERT INTO dicom_instance (
                    patient_identifier,
                    modality,
                    study_uid,
                    sop_instance_uid
                )
                VALUES (:patientIdentifier, :modality, :studyUid, :sopInstanceUid)
                ON CONFLICT (sop_instance_uid) DO NOTHING
                """)
                .setParameter("patientIdentifier", data.patientIdentifier())
                .setParameter("modality", data.modality())
                .setParameter("studyUid", data.studyUid())
                .setParameter("sopInstanceUid", data.sopInstanceUid())
                .executeUpdate();
    }
}