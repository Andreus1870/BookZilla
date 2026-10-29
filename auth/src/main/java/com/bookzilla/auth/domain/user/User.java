package com.bookzilla.auth.domain.user;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID id;

    private final UUID keycloakId;

    private final String firstName;

    private final String lastName;

    private final String email;

    private String phone;

    private String country;

    private String city;

    private final Instant registrationDate;

    private boolean isDeleted;


    public User(String firstName,
                String lastName,
                String email,
                UUID keycloakId) {

        this.id = UUID.randomUUID();
        this.keycloakId = keycloakId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = Instant.now();
        this.isDeleted = false;
    }

    public User(UUID id,
                UUID keycloakId,
                String firstName,
                String lastName,
                String email,
                String phone,
                String country,
                String city,
                Instant registrationDate,
                boolean isDeleted) {

        this.id = id;
        this.keycloakId = keycloakId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.registrationDate = registrationDate;
        this.isDeleted = isDeleted;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public UUID getKeycloakId() {
        return keycloakId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void updateAdditionalInfo(String country,
                                     String city,
                                     String phone) {
        this.country = country;
        this.city = city;
        this.phone = phone;
    }
}
