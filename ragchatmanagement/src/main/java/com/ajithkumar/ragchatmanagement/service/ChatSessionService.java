package com.ajithkumar.ragchatmanagement.service;

import com.ajithkumar.ragchatmanagement.dto.ChatSessionResponse;
import com.ajithkumar.ragchatmanagement.dto.CreateChatSessionRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;

import java.util.UUID;

/**
 * Service interface for chat session operations.
 */
public interface ChatSessionService {

    /**
     * Create a new chat session for a user.
     *
     * @param request the create chat session request containing user ID and session name
     * @return the created chat session response
     */
    ChatSession createChatSession(CreateChatSessionRequest request);

    /**
     * Retrieve a chat session by ID.
     *
     * @param sessionId the ID of the chat session
     * @return the chat session response if found
     */
    ChatSession getChatSessionById(UUID sessionId);

    /**
     * Verify if a user exists.
     *
     * @param userId the ID of the user
     * @return true if user exists, false otherwise
     */
    boolean userExists(UUID userId);

    ChatSession renameSession(UUID sessionId, String newName);

    ChatSession setFavourite(UUID sessionId, boolean favourite);

}

