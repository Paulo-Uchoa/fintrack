CREATE TABLE users (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(120)  NOT NULL,
    email      VARCHAR(180)  NOT NULL,
    password   VARCHAR(100)  NOT NULL,
    role       VARCHAR(20)   NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE accounts (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(120)   NOT NULL,
    type            VARCHAR(20)    NOT NULL,
    initial_balance NUMERIC(15, 2) NOT NULL DEFAULT 0,
    archived        BOOLEAN        NOT NULL DEFAULT FALSE,
    user_id         BIGINT         NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_accounts_user ON accounts (user_id);

CREATE TABLE categories (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL,
    type       VARCHAR(20)  NOT NULL,
    color      VARCHAR(7),
    user_id    BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_categories_user_name_type UNIQUE (user_id, name, type)
);
CREATE INDEX idx_categories_user ON categories (user_id);

CREATE TABLE transactions (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(180)   NOT NULL,
    amount      NUMERIC(15, 2) NOT NULL,
    date        DATE           NOT NULL,
    type        VARCHAR(20)    NOT NULL,
    account_id  BIGINT         NOT NULL,
    category_id BIGINT         NOT NULL,
    user_id     BIGINT         NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT fk_transactions_account  FOREIGN KEY (account_id)  REFERENCES accounts (id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_transactions_user     FOREIGN KEY (user_id)     REFERENCES users (id),
    CONSTRAINT chk_transactions_amount  CHECK (amount > 0)
);
CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);
CREATE INDEX idx_transactions_account   ON transactions (account_id);
CREATE INDEX idx_transactions_category  ON transactions (category_id);

CREATE TABLE budgets (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference_month DATE           NOT NULL,
    limit_amount    NUMERIC(15, 2) NOT NULL,
    category_id     BIGINT         NOT NULL,
    user_id         BIGINT         NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_budgets_user     FOREIGN KEY (user_id)     REFERENCES users (id),
    CONSTRAINT uk_budgets_user_category_month UNIQUE (user_id, category_id, reference_month),
    CONSTRAINT chk_budgets_limit   CHECK (limit_amount > 0)
);
CREATE INDEX idx_budgets_user ON budgets (user_id);
