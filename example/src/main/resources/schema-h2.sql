create table if not exists employee(
  id varchar(255) not null,
  firstName varchar(255),
  lastName varchar (255),
  birthDate timestamp NULL DEFAULT NULL,
  age int,
  primary key (id),
);

create table if not exists address(
  id varchar(255) not null,
  employee_id varchar (255),
  street varchar(255),
  city varchar (255),
  state varchar (255),
  zipCode int,
  primary key (idList),
  CONSTRAINT employee_id_fkey FOREIGN KEY (employee_id) REFERENCES employee (id));
);