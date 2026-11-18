package com.demo.repository;

import com.demo.entity.Hl7Message;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Hl7MessageRepo
        implements PanacheRepository<Hl7Message> {
}
