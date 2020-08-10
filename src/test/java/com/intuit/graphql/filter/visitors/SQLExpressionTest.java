package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.common.TestConstants;
import graphql.ExecutionResult;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.junit.Assert;
import org.junit.Test;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class SQLExpressionTest extends BaseFilterExpressionTest {

    @Override
    public RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("searchEmployees", getEmployeeDataFetcher().searchEmployeesSQL()))
                .build();
    }
    @Test
    public void filterExpressionSimple() {
        System.out.println("Query: " + TestConstants.BINARY_FILER);
        ExecutionResult result = getGraphQL().execute(TestConstants.BINARY_FILER);

        String expectedExpression = "WHERE (empFirstName LIKE '%Saurabh%')";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionWithOR() {
        System.out.println("Query: " + TestConstants.COMPOUND_FILER_WITH_OR);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR);

        String expectedExpression = "WHERE ((empFirstName LIKE '%Saurabh%') OR (lastName = 'Jaiswal'))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionWithAND() {
        System.out.println("Query: " + TestConstants.COMPOUND_FILER_WITH_AND);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND);

        String expectedExpression = "WHERE ((empFirstName LIKE '%Saurabh%') AND (lastName = 'Jaiswal'))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionORWithAND() {
        System.out.println("Query: " + TestConstants.COMPOUND_FILER_WITH_OR_AND);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_AND);

        String expectedExpression = "WHERE ((empFirstName LIKE '%Saurabh%') OR ((lastName = 'Jaiswal') AND (age >= '25')))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionANDWithOR() {
        System.out.println("Query: " + TestConstants.COMPOUND_FILER_WITH_AND_OR);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_OR);

        String expectedExpression = "WHERE ((empFirstName LIKE '%Saurabh%') AND ((lastName = 'Jaiswal') OR (age >= '25')))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionANDWithMultipleOR() {
        System.out.println("Query: " + TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);

        String expectedExpression = "WHERE (((empFirstName LIKE '%Saurabh%') OR (lastName = 'Jaiswal')) OR ((empFirstName = 'Vinod') AND (age >= '30')))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionORWithMultipleAND() {
        System.out.println("Query: " + TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);

        String expectedExpression = "WHERE (((empFirstName LIKE '%Saurabh%') AND (lastName = 'Jaiswal')) AND ((empFirstName = 'Vinod') OR (age >= '30')))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionWithDate () {
        System.out.println("Query: " + TestConstants.COMPOUND_DATE_FILTER);
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_DATE_FILTER);

        String expectedExpression = "WHERE ((lastName = 'Jaiswal') OR (birthDate > 'Thu Dec 19 16:39:57 PST 1996'))";
        System.out.println("");
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionWithOtherArgs () {
        System.out.println("Query: " + TestConstants.FILTER_WITH_OTHER_ARGS);
        ExecutionResult result = getGraphQL().execute(TestConstants.FILTER_WITH_OTHER_ARGS);
        String parent = getEmployeeDataFetcher().getSqlExpression();

        String expectedExpression = "WHERE (empFirstName = 'Saurabh')";
        System.out.println("SQL: "+expectedExpression);
        Assert.assertEquals(expectedExpression,parent);
    }

    @Test
    public void filterExpressionWithVariables () {
        System.out.println("\nQuery: " + TestConstants.FILTER_WITH_VARIABLE);
        ExecutionResult result = getGraphQL().execute(TestConstants.FILTER_WITH_VARIABLE);
        String parent = getEmployeeDataFetcher().getSqlExpression();

        String expectedExpression = "WHERE ((empFirstName LIKE '%Saurabh%') AND (lastName = 'Jaiswal'))";
        System.out.println("SQL: "+expectedExpression);
        Assert.assertEquals(expectedExpression,parent);
    }

    @Test
    public void filterExpressionWithLastNameIn () {
        System.out.println("\nQuery: " + TestConstants.LAST_NAME_IN);
        ExecutionResult result = getGraphQL().execute(TestConstants.LAST_NAME_IN);

        String expectedExpression = "WHERE (lastName IN ('Jaiswal', 'Gupta', 'Kumar'))";
        System.out.println("SQL: "+expectedExpression);
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void filterExpressionWithAgeIn () {
        System.out.println("\nQuery: " + TestConstants.AGE_IN);
        ExecutionResult result = getGraphQL().execute(TestConstants.AGE_IN);

        String expectedExpression = "WHERE (age IN ('32', '35', '40'))";
        System.out.println("SQL: "+expectedExpression);
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

    @Test
    public void invalidFilterExpression () {
        System.out.println("\nQuery: " + TestConstants.INVALID_FILTER);
        ExecutionResult result = getGraphQL().execute(TestConstants.INVALID_FILTER);

        String expectedExpression = null;
        System.out.println("SQL: "+expectedExpression);
        Assert.assertEquals(expectedExpression,getEmployeeDataFetcher().getSqlExpression());
    }

}
