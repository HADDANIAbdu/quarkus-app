package com.demo.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "dicom_instance")
public class DicomInstance extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "patient_identifier", nullable = false, length = 64)
    public String patientIdentifier;

    @Column(name = "modality", nullable = false, length = 16)
    public String modality;

    @Column(name = "study_uid", nullable = false, length = 64)
    public String studyUid;

    @Column(name = "sop_instance_uid", nullable = false, unique = true, length = 64)
    public String sopInstanceUid;
}
