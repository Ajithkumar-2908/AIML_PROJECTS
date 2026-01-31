package com.ajithkumar.ragchatmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ChatSession entity representing a chat session in the RAG chat management system.
 */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
@Entity
@Table(name = "chatsession")
public class ChatSession {
    public ChatSession() {
    }

    public ChatSession(UUID userId, Boolean isFavourite, String sessionName, LocalDateTime createdDate, LocalDateTime updatedDate) {

        this.userId = userId;
        this.isFavourite = isFavourite;
        this.sessionName = sessionName;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "userid", nullable = false)
    private UUID userId;

    @Column(name = "isfavourite", nullable = false)
    private Boolean isFavourite;

    @Column(name = "sessionname", nullable = false)
    private String sessionName;

    @Column(name = "createddate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updateddate", nullable = false)
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
        message.setChatSession(this);
    }

    public void removeMessage(ChatMessage message) {
        chatMessages.remove(message);
        message.setChatSession(null);
    }
}
