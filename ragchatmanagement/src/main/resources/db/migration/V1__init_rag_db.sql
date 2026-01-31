-- =========================
-- USER TABLE
-- =========================
CREATE TABLE "user" (
    id              UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    lastlogindate   TIMESTAMP,
    createddate     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updateddate     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookup by email
CREATE INDEX idx_user_email ON "user"(email);


-- =========================
-- CHAT SESSION TABLE
-- =========================
CREATE TABLE chatsession (
    id              UUID PRIMARY KEY,
    userid          UUID NOT NULL,
    isfavourite     BOOLEAN NOT NULL DEFAULT FALSE,
    sessionname     VARCHAR(255) NOT NULL,
    createddate     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updateddate     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chatsession_user
        FOREIGN KEY (userid)
        REFERENCES "user"(id)
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_chatsession_userid ON chatsession(userid);

-- =========================
-- CHAT MESSAGE TABLE
-- =========================
CREATE TABLE chatmessage (
    id              UUID PRIMARY KEY,
    chatsessionid   UUID NOT NULL,
    content         TEXT NOT NULL,
    context         JSONB,
    attachment      TEXT,
    createddate     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updateddate     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chatmessage_chatsession
        FOREIGN KEY (chatsessionid)
        REFERENCES chatsession(id)
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_chatmessage_chatsessionid ON chatmessage(chatsessionid);
