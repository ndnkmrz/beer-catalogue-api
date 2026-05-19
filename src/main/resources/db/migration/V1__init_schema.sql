CREATE TABLE manufacturers
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL
);

CREATE TABLE beers
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255)     NOT NULL,
    abv             DOUBLE PRECISION NOT NULL,
    description     TEXT,
    type            VARCHAR(50)      NOT NULL,
    manufacturer_id BIGINT           NOT NULL,
    CONSTRAINT fk_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES manufacturers (id)
);