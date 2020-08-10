# Graphql Filter Demo With JPA Specification

This examples demonstrates the usage of graphql-filter-java library for transforming the input filter expression supplied with a graphql query into JPA Specification criteria for any SQL database.
By using the filter library, developers can skip the complexity of parsing and transforming the input filter expression into desired format by just few lines of code as shown below:
 
#### GraphQL Schema

```
scalar DateTime

type Query {
   searchEmployees(filter: EmployeeFilter): [Employee!]
 }

# Define the types
type Employee {
   id: ID
   firstName: String!
   lastName: String!
   age: Int!
   birthDate: DateTime!
   address (filter: AddressFilter): [Address!]
}

type Address {
   id: ID
   street : String!
   city : String!
   state : String!
   zipCode : Int!
}

# Define filter input
input EmployeeFilter {
   firstName: StringExpression
   lastName: StringExpression
   age: IntExpression
   birthDate: DateTimeExpression

   and: [EmployeeFilter!]
   or: [EmployeeFilter!]
   not: EmployeeFilter
}

input AddressFilter {
   street : StringExpression
   city : StringExpression
   state : StringExpression
   zipCode : IntExpression

     and: [AddressFilter!]
     or: [AddressFilter!]
     not: AddressFilter
}


# Define String expression
input StringExpression {
   equals: String
   contains: String
   in: [String!]
}

# Define Int Expression
input IntExpression {
   eq: Int
   gt: Int
   gte: Int
   lt: Int
   lte: Int
   in: [Int!]
   between: [Int!]
}

input DateTimeExpression {
    eq: DateTime
    gt: DateTime
    gte: DateTime
    lt: DateTime
    lte: DateTime
    between: [DateTime!]
}

```
#### Filter Dependency
```xml
<dependency>
  <groupId>com.intuit.idg.graphql</groupId>
  <artifactId>graphql-filter-java</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

#### Database Setup
##### Database schema
```sql
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
```

##### EmployeeEntity.java
```java
package com.intuit.graphql.demo.persistence.entity;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class EmployeeEntity {
    @Id
    String id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Integer age;

    @OneToMany(mappedBy = "employee")
    private Set<AddressEntity> addresses;
}
```

##### EmployeeRepository.java
```java
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String>, JpaSpecificationExecutor<EmployeeEntity> {
}
```
##### EmployeeService.java
```java
@Component
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public  List<Map<String, String>> searchEmployees(Specification<EmployeeEntity> specification) {
        List<EmployeeEntity> employees = null;
        if (specification != null) {
           employees = employeeRepository.findAll(specification);
        } else {
            employees = employeeRepository.findAll();
        }
        List<Map<String,String>> employeesMap = new ArrayList<>();
        for (EmployeeEntity employeeEntity: employees) {
            Map<String, String> emp = new HashMap<>();
            emp.put("id", employeeEntity.getId());
            emp.put("firstName", employeeEntity.getFirstName());
            emp.put("lastName", employeeEntity.getLastName());
            emp.put("age", employeeEntity.getAge().toString());
            employeesMap.add(emp);
        }
        return employeesMap;
    }
}
```
##### EmployeeDataFetcher.java
```java
@Component
public class EmployeeDataFetcher {
    @Autowired
    private EmployeeService employeeService;

   public DataFetcher searchEmployees() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

                FilterExpression.FilterExpressionBuilder builder = FilterExpression.newFilterExpressionBuilder();
                FilterExpression filterExpression = builder.args(dataFetchingEnvironment.getArguments())
                        .build();

                Specification<EmployeeEntity> specification = filterExpression.getExpression(ExpressionFormat.JPA);
                List<Map<String, String>> employees = employeeService.searchEmployees(specification);
                return employees;
            }
        };
    }
```
#### Query With Filter
```
{
  searchEmployees(filter: {
    or: [
      {lastName: {contains: "Smith"}},
      {age: {lte: 30}}
    ]
  }) {
    id
    firstName
    lastName
    age
  }
}
```

#### Result
```
{
  "data": {
    "searchEmployees": [
      {
        "id": "4",
        "firstName": "Will",
        "lastName": "Smith",
        "age": 52
      },
      {
        "id": "5",
        "firstName": "Jack",
        "lastName": "Smith",
        "age": 25
      }
    ]
  }
}

```
