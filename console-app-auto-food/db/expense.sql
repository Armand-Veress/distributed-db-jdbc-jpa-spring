-- expense table

CREATE TABLE expense(
    id           BIGINT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  VARCHAR(255)   NOT NULL,
    amount       NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
    expense_date DATE           NOT NULL
);