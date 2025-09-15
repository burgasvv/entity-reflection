
--liquibase formatted sql

--changeset burgasvv:1
insert into company (id, name, description)
values ('e1202230-bedf-4325-9fdb-c4751f062a33', 'TeleSet', 'Описание компании TeleSet');
insert into company (id, name, description)
values ('d6da80de-3738-4717-a165-fbb456ade2e9', 'Computer Science', 'Описание компании Computer Science');