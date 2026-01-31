package com.ajithkumar.ragchatmanagement.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class CreateChatMessageRequest {

    @NotBlank
    private String content;

    private Map<String, Object> context;
    private String attachment;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
