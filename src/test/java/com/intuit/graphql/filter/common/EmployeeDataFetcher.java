/**
 * Copyright 2020 Intuit Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intuit.graphql.filter.common;

import com.intuit.graphql.filter.client.ExpressionFormat;
import com.intuit.graphql.filter.client.FilterExpression;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sjaiswal
 */
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
