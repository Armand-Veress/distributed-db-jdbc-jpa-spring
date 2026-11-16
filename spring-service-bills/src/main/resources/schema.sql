-- expense table

CREATE TABLE IF NOT EXISTS expense (
    id           BIGINT         AUTO_INCREMENT PRIMARY KEY,
    description  VARCHAR(255)   NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    expense_date DATE           NOT NULL
);