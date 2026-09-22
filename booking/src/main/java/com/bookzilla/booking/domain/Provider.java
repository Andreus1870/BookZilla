package com.bookzilla.booking.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "providers")
public class Provider {
    @Id
    private UUID id;

    public Provider() {
    }

    public Provider(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
