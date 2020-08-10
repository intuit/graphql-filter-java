package com.intuit.graphql.filter.common;

import com.intuit.graphql.filter.client.ExpressionFormat;
import com.intuit.graphql.filter.client.FilterExpression;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.Map;

public class EmployeeDataFetcher {

    private String expression;
    private String sqlExpression;
    private Specification<Object> specification;

    public EmployeeDataFetcher() {

    }

    public DataFetcher searchEmployees() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                FilterExpression.FilterExpressionBuilder builder = FilterExpression.newFilterExpressionBuilder();
                FilterExpression filterExpression = builder.field(dataFetchingEnvironment.getField())
                        .args(dataFetchingEnvironment.getArguments())
                        .build();
                expression = filterExpression.getExpression(ExpressionFormat.INFIX);
                return null;
            }
        };
    }

    public DataFetcher searchEmployeesSQL() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                FilterExpression.FilterExpressionBuilder builder = FilterExpression.newFilterExpressionBuilder();
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("firstName","empFirstName");
                FilterExpression filterExpression = builder.field(dataFetchingEnvironment.getField())
                        .map(fieldMap)
                        .args(dataFetchingEnvironment.getArguments())
                        .build();
                sqlExpression = filterExpression.getExpression(ExpressionFormat.SQL);
                return null;
            }
        };
    }

    public DataFetcher searchEmployeesJPA() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                FilterExpression.FilterExpressionBuilder builder = FilterExpression.newFilterExpressionBuilder();
                FilterExpression filterExpression = builder.field(dataFetchingEnvironment.getField())
                        .args(dataFetchingEnvironment.getArguments())
                        .build();
                specification = filterExpression.getExpression(ExpressionFormat.JPA);
                return null;
            }
        };
    }

    public String getExpression() {
        return expression;
    }

    public String getSqlExpression() {
        return sqlExpression;
    }

    public Specification<Object> getSpecification() {
        return specification;
    }
}
