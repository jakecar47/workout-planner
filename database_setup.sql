-- DDL queries to create the database schema for the web app
--
drop database if exists workout_app;
create database if not exists workout_app;
use workout_app;

-- USERS TABLE
create table if not exists users (
    id int auto_increment,
    email varchar(255) not null,
    uname varchar(255) not null,
    password varchar(255) not null,
    created_at timestamp default current_timestamp,
    primary key(id),
    unique(email),
    unique(uname)
);

-- WORKOUTS TABLE
create table if not exists workouts (
    id int auto_increment,
    name varchar(255) not null,
    user_id int not null,
    description text,
    startTime datetime,
    endTime datetime,
    created_at timestamp default current_timestamp,
    primary key(id),
    foreign key(user_id) references users(id) on delete cascade,
    unique(name)
);

-- EXERCISES TABLE
create table if not exists exercises (
    id int auto_increment,
    name varchar(255) not null,
    target_muscle varchar(255),
    description text,
    primary key(id),
    unique(name)
);

-- WORKOUT_EXERCISES TABLE
create table if not exists workout_exercises (
    workout_id int not null,
    exercise_id int not null,
    time int,
    sets int,
    reps int,
    primary key (workout_id, exercise_id),
    foreign key (workout_id) references workouts(id) on delete cascade,
    foreign key (exercise_id) references exercises(id) on delete cascade
);

-- GOALS TABLE
create table if not exists goals (
    id int auto_increment,
    description varchar(255) not null,
    user_id int not null,
    exercise_id int,
    created_at timestamp default current_timestamp,
    primary key(id),
    foreign key (user_id) references users(id) on delete cascade,
    foreign key (exercise_id) references exercises(id) on delete cascade
);
