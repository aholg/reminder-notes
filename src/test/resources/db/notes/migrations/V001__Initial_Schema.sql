create table if not exists Notes(
    id serial primary key,
    title varchar(50) not null,
    content varchar(50) not null
)