package com.demo.repository;

import com.demo.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientRepo implements PanacheRepository<Patient> {
}
