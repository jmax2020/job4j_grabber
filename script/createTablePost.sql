create table post(
    id serial primary key,
    link text not null,
    unique (link),
    name text,
	text text,
    created date
);



