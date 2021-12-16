CREATE TABLE customer (
    id BIGINT auto_increment,
    name VARCHAR(100) NOT NULL
);

ALTER TABLE customer
    ADD CONSTRAINT CUSTOMER_NAME_UNIQUE UNIQUE(name);

CREATE TABLE product (
    id BIGINT auto_increment,
    name VARCHAR(100) NOT NULL
);

ALTER TABLE product
    ADD CONSTRAINT PRODUCT_NAME_UNIQUE UNIQUE(name);

CREATE TABLE contract (
    id BIGINT auto_increment,
    reference VARCHAR(100) NOT NULL,
    customer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL
);

ALTER TABLE contract
    ADD CONSTRAINT CONTRACT_REFERENCE_UNIQUE UNIQUE(reference);

ALTER TABLE contract
    ADD FOREIGN KEY (customer_id)
    REFERENCES customer(id);

ALTER TABLE contract
    ADD FOREIGN KEY (product_id)
    REFERENCES product(id);