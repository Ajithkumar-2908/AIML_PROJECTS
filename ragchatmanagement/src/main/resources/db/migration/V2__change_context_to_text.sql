-- =========================
-- ALTER CONTEXT COLUMN TYPE TO TEXT
-- =========================

ALTER TABLE chatmessage
    ALTER COLUMN context DROP DEFAULT;

ALTER TABLE chatmessage
    ALTER COLUMN context TYPE TEXT USING context::TEXT;

ALTER TABLE chatmessage
    ALTER COLUMN context DROP NOT NULL;
