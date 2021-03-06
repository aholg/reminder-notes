create table if not exists Users(
    id serial primary key,
    username varchar(50) unique
);

create table if not exists Notes(
    id serial primary key,
    username varchar references users (username),
    title varchar(50) not null,
    content varchar(50) not null
);
