package com.demo.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "hl7_message")
public class Hl7Message extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    public Patient patient;

    @Column(name = "raw_message", nullable = false, columnDefinition = "TEXT")
    public String rawMessage;

    @Column(name = "received_at", nullable = false)
    public Instant receivedAt;
}
