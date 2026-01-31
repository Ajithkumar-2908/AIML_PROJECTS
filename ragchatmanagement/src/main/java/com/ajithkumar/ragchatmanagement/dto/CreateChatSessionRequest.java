package com.ajithkumar.ragchatmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a new chat session.
 */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class CreateChatSessionRequest {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotBlank(message = "Session name cannot be blank")
    private String sessionName;

    @Builder.Default
    private Boolean isFavourite = false;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Boolean favourite) {
        isFavourite = favourite;
    }
}
