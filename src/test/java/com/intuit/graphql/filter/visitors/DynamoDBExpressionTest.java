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

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * @author sjaiswal
 */
public class DynamoDBExpressionTest extends BaseFilterExpressionTest {

    @Override
    public RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("searchEmployees", getEmployeeDataFetcher().searchEmployeesDynamoDB()))
                .build();
    }

    @Test
    public void filterExpressionWithContains() {
        getGraphQL().execute(TestConstants.BINARY_FILER);

        String expectedExpression = "(contains(firstName, :firstName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithOR() {
        getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR);

        String expectedExpression = "((contains(firstName, :firstName)) OR (lastName = :lastName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithAND() {
        getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND);

        String expectedExpression = "((contains(firstName, :firstName)) AND (lastName = :lastName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionORWithAND() {
        getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_AND);

        String expectedExpression = "((contains(firstName, :firstName)) OR ((lastName = :lastName) AND (age >= :min_age)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionANDWithOR() {
        getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_OR);

        String expectedExpression = "((contains(firstName, :firstName)) AND ((lastName = :lastName) OR (age >= :min_age)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionANDWithMultipleOR() {
        getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);

        String expectedExpression = "(((contains(firstName, :firstName)) OR (lastName = :lastName)) OR ((firstName = :firstName) AND (age >= :min_age)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionORWithMultipleAND() {
        getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);

        String expectedExpression = "(((contains(firstName, :firstName)) AND (lastName = :lastName)) AND ((firstName = :firstName) OR (age >= :min_age)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithOtherArgs() {
        getGraphQL().execute(TestConstants.FILTER_WITH_OTHER_ARGS);

        String expectedExpression = "(firstName = :firstName)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithVariables() {
        getGraphQL().execute(TestConstants.FILTER_WITH_VARIABLE);

        String expectedExpression = "((contains(firstName, :firstName)) AND (lastName = :lastName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithLastNameIn() {
        getGraphQL().execute(TestConstants.LAST_NAME_IN);

        String expectedExpression = "(lastName IN (:lastName_0, :lastName_1, :lastName_2))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithAgeIn() {
        getGraphQL().execute(TestConstants.AGE_IN);

        String expectedExpression = "(age IN (:age_0, :age_1, :age_2))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void invalidFilterExpression() {
        getGraphQL().execute(TestConstants.INVALID_FILTER);

        String expectedExpression = null;

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void notFilterExpression() {
        getGraphQL().execute(TestConstants.NOT_FILTER);

        String expectedExpression = "( NOT (firstName = :firstName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void notCompoundFilterExpression() {
        getGraphQL().execute(TestConstants.NOT_COMPOUND_FILTER);

        String expectedExpression = "( NOT ((firstName = :firstName) AND (contains(lastName, :lastName))))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void compoundFilterExpressionWithNot() {
        getGraphQL().execute(TestConstants.COMPOUND_NOT_FILTER);

        String expectedExpression = "((firstName = :firstName) AND ( NOT (contains(lastName, :lastName))))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithStarts() {
        getGraphQL().execute(TestConstants.FIRST_NAME_STARTS);

        String expectedExpression = "(begins_with(firstName, :firstName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithContainsSubstring() {
        getGraphQL().execute(TestConstants.FIRST_NAME_CONTAINS);

        String expectedExpression = "(contains(firstName, :firstName))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithAfter() {
        getGraphQL().execute(TestConstants.BIRTH_DATE_AFTER);

        String expectedExpression = "(birthDate > :min_birthDate)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithBefore() {
        getGraphQL().execute(TestConstants.BIRTH_DATE_BEFORE);

        String expectedExpression = "(birthDate < :max_birthDate)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithOnOrAfter() {
        getGraphQL().execute(TestConstants.BIRTH_DATE_ON_OR_AFTER);

        String expectedExpression = "(birthDate >= :min_birthDate)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithOnOrBefore() {
        getGraphQL().execute(TestConstants.BIRTH_DATE_ON_OR_BEFORE);

        String expectedExpression = "(birthDate <= :max_birthDate)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }

    @Test
    public void filterExpressionWithOn() {
        getGraphQL().execute(TestConstants.BIRTH_DATE_ON);

        String expectedExpression = "(birthDate = :birthDate)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getDynamoDBExpression());
    }
}
