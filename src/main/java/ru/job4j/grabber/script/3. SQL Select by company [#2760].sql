CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);
CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);
insert into company (id, name) values (1, 'gazprom');
insert into company (id, name) values (2, 'vtb');
insert into company (id, name) values (3, 'rusal');
insert into company (id, name) values (5, 'FSB');
insert into person (id, name, company_id) values (1, 'Ivanov', 1);
insert into person (id, name, company_id) values (2, 'Petrov', 2);
insert into person (id, name, company_id) values (3, 'Sidorov', 3);
insert into person (id, name, company_id) values (5, 'Lutsenko', 5);
insert into person (id, name, company_id) values (6, 'Valenkov', 5);
insert into person (id, name, company_id) values (7, 'Popkov', 2);

select p.id, p.name, c.name from person as p join company as c on p.company_id = c.id where c.id <> 5;

select  c.name, count(p.company_id) from person p join company c
on p.company_id = c.id group by c.name order by count(p.company_id) desc
FETCH FIRST 1 ROW WITH TIES ;

