package com.ajithkumar.ragchatmanagement.service;

import com.ajithkumar.ragchatmanagement.dto.ChatMessageResponse;
import com.ajithkumar.ragchatmanagement.dto.CreateChatMessageRequest;

import java.util.UUID;

public interface ChatMessageService {

    ChatMessageResponse createMessage(UUID sessionId, CreateChatMessageRequest request);

    void deleteMessage(UUID sessionId, UUID messageId);

}
