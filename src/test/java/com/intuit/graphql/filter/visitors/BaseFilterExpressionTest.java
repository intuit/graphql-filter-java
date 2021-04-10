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

import com.intuit.graphql.filter.common.EmployeeDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.junit.Before;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;


/**
 * @author sjaiswal
 */
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
