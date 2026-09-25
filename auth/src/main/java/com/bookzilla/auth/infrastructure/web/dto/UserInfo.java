package com.bookzilla.auth.infrastructure.web.dto;

public record UserInfo (String firstName,
                        String lastName,
                        String email,
                        String phone,
                        String country,
                        String city,
                        String role,
                        String registrationDate){
}
