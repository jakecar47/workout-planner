drop database if exists cs4370_term_project_db;
create database if not exists cs4370_term_project_db;

use cs4370_term_project_db;

create table user (
    id int auto_increment,
    username varchar(255) not null,
    password varchar(255) not null,
    primary key (id),
    unique (username)
)