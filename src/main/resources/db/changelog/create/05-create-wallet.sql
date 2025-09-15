
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists wallet (
    id uuid default gen_random_uuid() unique not null ,
    balance decimal not null default 0 check ( balance >= 0 ) ,
    identity_id uuid not null references identity(id) on delete cascade on update cascade
);