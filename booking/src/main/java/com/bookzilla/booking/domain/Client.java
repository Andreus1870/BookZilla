package com.bookzilla.booking.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    private UUID id;

    public Client() {
    }

    public Client(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
