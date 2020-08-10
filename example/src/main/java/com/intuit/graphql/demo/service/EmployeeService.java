package com.intuit.graphql.demo.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.intuit.graphql.demo.persistence.entity.EmployeeEntity;
import com.intuit.graphql.demo.persistence.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

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
            Date date = employeeEntity.getBirthDate();
            OffsetDateTime offsetDateTime = date.toInstant()
                    .atOffset(ZoneOffset.UTC);
           // emp.put("birthDate", formatter.format(date));
            emp.put("birthDate", dateTimeFormatter.format(offsetDateTime));
            employeesMap.add(emp);
        }
        return employeesMap;
    }
}
