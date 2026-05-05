CREATE TABLE users (
    id UUID not null primary key,
    username VARCHAR(50) not null unique,
    name varchar(100) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    role varchar(20) not null check (role IN ('ADMIN','USER')),
    created_at TIMESTAMP WITH TIME ZONE not null default now()
);