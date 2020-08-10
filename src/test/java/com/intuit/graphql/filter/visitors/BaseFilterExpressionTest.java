package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.common.EmployeeDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.junit.Before;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseFilterExpressionTest {

    private GraphQL graphQL;
    private EmployeeDataFetcher employeeDataFetcher;

    @Before
    public void init() throws IOException {

        employeeDataFetcher = new EmployeeDataFetcher();

        String filePath = getClass().getClassLoader().getResource("schema.graphql").getPath();
        String sdl = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8).
                collect(Collectors.joining(System.lineSeparator()));

        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl){
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    protected abstract RuntimeWiring buildWiring();

    public GraphQL getGraphQL() {
        return graphQL;
    }

    public EmployeeDataFetcher getEmployeeDataFetcher() {
        return employeeDataFetcher;
    }
}
