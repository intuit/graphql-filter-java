-- Insert data into employee table.
insert into employee (id, first_name, last_name, birth_date, age) values ('1', 'Tom', 'Hanks', '1982-07-10', 38);
insert into employee (id, first_name, last_name, birth_date, age) values ('2', 'Johnny', 'Depp', '1970-06-04', 50);
insert into employee (id, first_name, last_name, birth_date, age) values ('3', 'Tom', 'Cruise', '1980-06-04', 40);
insert into employee (id, first_name, last_name, birth_date, age) values ('4', 'Will', 'Smith', '1968-06-04', 52);
insert into employee (id, first_name, last_name, birth_date, age) values ('5', 'Jack', 'Smith', '1995-06-04', 25);

-- Insert data into address table.
insert into address (id, employee_id, street, city, state, zip_code) values ('1', '1', '3000 Abs St', 'Newark','CA', 94560);
insert into address (id, employee_id, street, city, state, zip_code) values ('2', '2', '400 Xyz Way', 'Sunnyvale','CA', 94087);
insert into address (id, employee_id, street, city, state, zip_code) values ('3', '1', '400 Xyz Way', 'Sunnyvale','CA', 94087);
insert into address (id, employee_id, street, city, state, zip_code) values ('4', '3', '100 Pqr Way', 'Fremont','CA', 94050);
insert into address (id, employee_id, street, city, state, zip_code) values ('5', '4', '456 Pqr Blvd', 'Fremont','CA', 94566);