package com.bookzilla.auth.application.dto;

public record UserInfo (String firstName,
                        String lastName,
                        String email,
                        String phone,
                        String country,
                        String city,
                        String registrationDate){
}
