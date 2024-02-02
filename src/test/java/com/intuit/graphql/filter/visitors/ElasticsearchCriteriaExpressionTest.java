/*
  Copyright 2020 Intuit Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.common.TestConstants;
import graphql.ExecutionResult;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.query.Criteria;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * @author Sohan Lal
 */
public class ElasticsearchCriteriaExpressionTest extends BaseFilterExpressionTest {

    @Override
    protected RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("searchEmployees", getEmployeeDataFetcher().searchEmployeesElasticsearch()))
                .build();
    }

    @Test
    public void filterExpressionSimple() {
        getGraphQL().execute(TestConstants.BINARY_FILER);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh");

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionSimpleInt() {
        getGraphQL().execute(TestConstants.BINARY_FILER_INT);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = new Criteria("age").greaterThanEqual(25);

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh")
                .or(Criteria.where("lastName").expression("\"Jaiswal\""));

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh")
                .and(Criteria.where("lastName").expression("\"Jaiswal\""));

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionORWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_AND);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh")
                .or(Criteria.where("lastName").expression("\"Jaiswal\"")
                        .and(Criteria.where("age").greaterThanEqual(25)));

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionANDWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_OR);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh")
                .and(Criteria.where("lastName").expression("\"Jaiswal\"")
                        .or(Criteria.where("age").greaterThanEqual(25)));

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionANDWithMultipleOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh")
                .or(Criteria.where("lastName").expression("\"Jaiswal\""))
                .or(Criteria.where("firstName").expression("\"Vinod\"")
                        .and(Criteria.where("age").greaterThanEqual(30)));

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void filterExpressionORWithMultipleAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);

        final Criteria actualCriteria = getEmployeeDataFetcher().getElasticsearchCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").contains("Saurabh")
                .and(Criteria.where("lastName").expression("\"Jaiswal\""))
                .and(Criteria.where("firstName").expression("\"Vinod\"")
                        .or(Criteria.where("age").greaterThanEqual(30)));

        Assert.assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

}