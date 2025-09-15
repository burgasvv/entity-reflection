
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity (
    id uuid default gen_random_uuid() unique not null ,
    authority varchar not null ,
    username varchar unique not null ,
    password varchar not null ,
    firstname varchar not null ,
    lastname varchar not null ,
    patronymic varchar not null ,
    company_id uuid references company(id) on delete set null on update cascade
)