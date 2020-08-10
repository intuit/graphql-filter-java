package com.intuit.graphql.demo.datafetchers;

import com.intuit.graphql.demo.context.DataFetcherContext;
import com.intuit.graphql.demo.persistence.entity.AddressEntity;
import com.intuit.graphql.demo.persistence.entity.EmployeeEntity;
import com.intuit.graphql.demo.service.AddressService;
import com.intuit.graphql.demo.service.EmployeeService;

import com.intuit.graphql.filter.client.ExpressionFormat;
import com.intuit.graphql.filter.client.FilterExpression;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;

/**
 * This data fetcher demonstrate the n+1 problem.
 */
@Component
public class EmployeeDataFetcher {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AddressService addressService;

    private static ThreadLocal<String> dataFetcherContext = new ThreadLocal<>();


    public DataFetcher searchEmployees() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

                FilterExpression.FilterExpressionBuilder builder = FilterExpression.newFilterExpressionBuilder();
                FilterExpression filterExpression = builder.args(dataFetchingEnvironment.getArguments())
                        .build();

                Specification<EmployeeEntity> specification = filterExpression.getExpression(ExpressionFormat.JPA);

                List<Map<String, String>> employees = employeeService.searchEmployees(specification);
                dataFetcherContext.set(employees.size()+"");
                System.out.println("Fetched employees from db, threadId = "+Thread.currentThread().getId()+", size = "+employees.size());
                System.out.println("Hashcode: "+this.hashCode());
                return employees;
            }
        };
    }

    public DataFetcher getAddresses() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Map<String,String> employee = dataFetchingEnvironment.getSource();
                String employeeId = employee.get("id");
                FilterExpression.FilterExpressionBuilder builder = FilterExpression.newFilterExpressionBuilder();
                FilterExpression filterExpression = builder.args(dataFetchingEnvironment.getArguments())
                        .build();
                Specification<AddressEntity> specification = filterExpression.getExpression(ExpressionFormat.JPA);
                Specification<AddressEntity> joinSpecification = new Specification<AddressEntity>() {

                    @Override
                    public Predicate toPredicate(Root<AddressEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                        From from = root;
                        Path path = from.join("employee").get("id");
                        Predicate predicate = criteriaBuilder.equal(path, employeeId);
                        return predicate;
                    }
                };
                if (specification != null) {
                    /* Create a join condition between Address and Employee*/
                    joinSpecification =  specification.and(joinSpecification);
                }
                List<Map<String, String>> address = addressService.searchAddresses(joinSpecification);
                System.out.println("Fetched address from db, threadId = "+Thread.currentThread().getId()+", employeeId = "+employeeId + "size = "+dataFetcherContext.get());
                return address;
            }
        };
    }
}
