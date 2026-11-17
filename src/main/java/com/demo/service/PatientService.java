package com.demo.service;

import com.demo.entity.Patient;
import com.demo.repository.PatientRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PatientService {
    private final PatientRepo patientRepo;

    @Inject
    public PatientService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    public Patient findByPatientIdentifier(String patientIdentifier) {
        return patientRepo.find("patientIdentifier", patientIdentifier).firstResult();
    }
}
