CREATE TABLE rod.failed_http_calls
(
    id              UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    url             TEXT      NOT NULL UNIQUE,
    created_at      TIMESTAMP NOT NULL,
    last_attempt_at TIMESTAMP NULL,
    attempts        INT       NOT NULL DEFAULT 0
);