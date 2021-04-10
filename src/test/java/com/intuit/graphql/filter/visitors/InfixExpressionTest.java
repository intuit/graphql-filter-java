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
public class InfixExpressionTest extends BaseFilterExpressionTest{

    @Override
    public RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("searchEmployees", getEmployeeDataFetcher().searchEmployees()))
                .build();
    }

    @Test
    public void filterExpressionSimple() {
        ExecutionResult result = getGraphQL().execute(TestConstants.BINARY_FILER);

        String expectedExpression = "(firstName contains Saurabh)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionSimpleInt() {
        ExecutionResult result = getGraphQL().execute(TestConstants.BINARY_FILER_INT);

        String expectedExpression = "(age gte 25)";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR);

        String expectedExpression = "((firstName contains Saurabh) or (lastName equals Jaiswal))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND);

        String expectedExpression = "((firstName contains Saurabh) and (lastName equals Jaiswal))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionORWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_AND);

        String expectedExpression = "((firstName contains Saurabh) or ((lastName equals Jaiswal) and (age gte 25)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionANDWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_OR);

        String expectedExpression = "((firstName contains Saurabh) and ((lastName equals Jaiswal) or (age gte 25)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionANDWithMultipleOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);

        String expectedExpression = "(((firstName contains Saurabh) or (lastName equals Jaiswal)) or ((firstName equals Vinod) and (age gte 30)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionORWithMultipleAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);

        String expectedExpression = "(((firstName contains Saurabh) and (lastName equals Jaiswal)) and ((firstName equals Vinod) or (age gte 30)))";

        Assert.assertEquals(expectedExpression, getEmployeeDataFetcher().getExpression());
    }

    @Test
    public void filterExpressionWithVariables () {
        ExecutionResult result = getGraphQL().execute(TestConstants.FILTER_WITH_VARIABLE);
        String parent = getEmployeeDataFetcher().getExpression();

        String expectedExpression = "((firstName contains Saurabh) and (lastName equals Jaiswal))";

        Assert.assertEquals(expectedExpression,parent);
    }

}