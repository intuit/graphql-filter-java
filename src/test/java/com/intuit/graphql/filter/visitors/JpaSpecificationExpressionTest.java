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
package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.common.TestConstants;
import graphql.ExecutionResult;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * @author sjaiswal
 */
public class JpaSpecificationExpressionTest extends BaseFilterExpressionTest {


    @Override
    protected RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("searchEmployees", getEmployeeDataFetcher().searchEmployeesJPA()))
                .build();
    }

    @Test
    public void filterExpressionSimple() {
        ExecutionResult result = getGraphQL().execute(TestConstants.BINARY_FILER);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionSimpleInt() {
        ExecutionResult result = getGraphQL().execute(TestConstants.BINARY_FILER_INT);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionORWithAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_AND);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionANDWithOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_OR);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionANDWithMultipleOR() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_OR_OR_AND);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }

    @Test
    public void filterExpressionORWithMultipleAND() {
        ExecutionResult result = getGraphQL().execute(TestConstants.COMPOUND_FILER_WITH_AND_AND_OR);

        Specification<Object> specification = getEmployeeDataFetcher().getSpecification();

        Assert.assertNotNull(specification);
    }



}