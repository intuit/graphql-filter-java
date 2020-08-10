package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.common.TestConstants;
import graphql.ExecutionResult;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.junit.Assert;
import org.junit.Test;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static org.junit.Assert.*;

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
        System.out.println("\nQuery: " + TestConstants.FILTER_WITH_VARIABLE);
        ExecutionResult result = getGraphQL().execute(TestConstants.FILTER_WITH_VARIABLE);
        String parent = getEmployeeDataFetcher().getExpression();

        String expectedExpression = "((firstName contains Saurabh) and (lastName equals Jaiswal))";
        System.out.println("SQL: "+expectedExpression);
        Assert.assertEquals(expectedExpression,parent);
    }

}