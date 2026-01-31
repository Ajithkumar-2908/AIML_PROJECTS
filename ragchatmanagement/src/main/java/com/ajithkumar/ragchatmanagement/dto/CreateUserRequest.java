package com.ajithkumar.ragchatmanagement.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateUserRequest {


    @NotNull(message = "User Name cannot be null")
    private String name;

    @NotNull(message = "User Email cannot be null")
    private String email;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }


}
