
--liquibase formatted sql

--changeset burgasvv:1
begin ;
insert into machine(name, description, cost)
values ('Test Mac 12', 'Описание машины Test Mac 12', 150500);
insert into machine(name, description, cost)
values ('Sim Four', 'Описание машины Sim Four', 180250);
insert into machine(name, description, cost)
values ('Test Shim 6', 'Описание машины Test Shim 6', 120300);
insert into machine(name, description, cost)
values ('Test Manager', 'Описание машины Test Manager', 110600);
insert into machine(name, description, cost)
values ('WIPost', 'Описание машины WIPost', 90400);
insert into machine(name, description, cost)
values ('Creator6000', 'Описание машины Creator6000', 164238);
commit ;