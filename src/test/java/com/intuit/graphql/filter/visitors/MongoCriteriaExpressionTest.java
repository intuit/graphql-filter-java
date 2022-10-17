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
import org.springframework.data.mongodb.core.query.Criteria;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * @author Sohan Lal
 */
public class MongoCriteriaExpressionTest extends BaseFilterExpressionTest {

    @Override
    protected RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("searchEmployees", getEmployeeDataFetcher().searchEmployeesMongo()))
                .build();
    }

    @Test
    public void filterExpressionSimple() {
        getGraphQL().execute(TestConstants.BINARY_FILER);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = Criteria.where("firstName").regex(".*Saurabh.*");

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionSimpleInt() {
        getGraphQL().execute(TestConstants.BINARY_FILER_INT);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = Criteria.where("age").gte(25);

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = new Criteria().orOperator(
                Criteria.where("firstName").regex(".*Saurabh.*"),
                Criteria.where("lastName").is("Jaiswal"));

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = new Criteria().andOperator(
                Criteria.where("firstName").regex(".*Saurabh.*"),
                Criteria.where("lastName").is("Jaiswal"));

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionORWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_AND);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = new Criteria().orOperator(
                Criteria.where("firstName").regex(".*Saurabh.*"),
                new Criteria().andOperator(
                        Criteria.where("lastName").is("Jaiswal"),
                        Criteria.where("age").gte(25)));

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionANDWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_OR);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = new Criteria().andOperator(
                Criteria.where("firstName").regex(".*Saurabh.*"),
                new Criteria().orOperator(
                        Criteria.where("lastName").is("Jaiswal"),
                        Criteria.where("age").gte(25)));

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionANDWithMultipleOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = new Criteria().orOperator(
                new Criteria().orOperator(
                        Criteria.where("firstName").regex(".*Saurabh.*"),
                        Criteria.where("lastName").is("Jaiswal")),
                new Criteria().andOperator(
                        Criteria.where("firstName").is("Vinod"),
                        Criteria.where("age").gte(30)));

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void filterExpressionORWithMultipleAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);

        final Criteria actualCriteria = getEmployeeDataFetcher().getMongoCriteria();
        final Criteria expectedCriteria = new Criteria().andOperator(
                new Criteria().andOperator(
                        Criteria.where("firstName").regex(".*Saurabh.*"),
                        Criteria.where("lastName").is("Jaiswal")),
                new Criteria().orOperator(
                        Criteria.where("firstName").is("Vinod"),
                        Criteria.where("age").gte(30)));

        assertCriteriaJsonEquals(expectedCriteria, actualCriteria);
    }

    private void assertCriteriaJsonEquals(final Criteria expected, final Criteria actual) {
        Assert.assertEquals(expected.getCriteriaObject().toJson(), actual.getCriteriaObject().toJson());
    }

}