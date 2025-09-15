
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity_machine (
    identity_id uuid references identity(id) on delete cascade on update cascade ,
    machine_id uuid references machine(id) on delete cascade on update cascade ,
    primary key (identity_id, machine_id)
);