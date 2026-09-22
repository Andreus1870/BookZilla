ALTER TABLE providers
    DROP CONSTRAINT providers_pkey;

ALTER TABLE providers
    DROP COLUMN user_id,
    DROP COLUMN id;

ALTER TABLE providers
    ADD COLUMN id UUID PRIMARY KEY;




ALTER TABLE clients
    DROP CONSTRAINT clients_pkey;

ALTER TABLE clients
    DROP COLUMN user_id,
    DROP COLUMN id;

ALTER TABLE clients
    ADD COLUMN id UUID PRIMARY KEY;