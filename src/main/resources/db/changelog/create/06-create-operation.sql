
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists operation (
    id uuid default gen_random_uuid() unique not null ,
    operation varchar not null ,
    amount decimal not null default 0 check ( amount >= 0 ) ,
    sender_wallet_id uuid references wallet(id) on delete set null on update cascade ,
    receiver_wallet_id uuid references wallet(id) on delete set null on update cascade
);