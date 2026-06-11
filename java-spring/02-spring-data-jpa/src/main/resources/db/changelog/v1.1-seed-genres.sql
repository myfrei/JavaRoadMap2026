--liquibase formatted sql

--changeset roadmap:1.1-seed-genres
insert into genres (name) values ('Фантастика');
insert into genres (name) values ('Детектив');
insert into genres (name) values ('Классика');
