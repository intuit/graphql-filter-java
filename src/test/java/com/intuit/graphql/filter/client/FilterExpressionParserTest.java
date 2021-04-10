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
package com.intuit.graphql.filter.client;

import com.intuit.graphql.filter.ast.Expression;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author sjaiswal
 */
public class FilterExpressionParserTest {

    @Test
    public void binaryExpressionTest() {
        // Create the mock data.
        Map<String, Object> argMaps = createBinaryFilterArgMap("firstName", "contains", "ABC");

        // Invoke method under test.
        FilterExpressionParser expressionParser = new FilterExpressionParser();
        Expression expression = expressionParser.parseFilterExpression(argMaps);

        // Verify assertions.
        Assert.assertNotNull(expression);
        Assert.assertEquals("(firstName contains ABC)", expression.infix());
    }

    @Test
    public void compoundExpressionTest() {
        // Create the mock data.
        Map<String, Object> argMaps = createCompoundFilterArgMap();

        // Invoke method under test.
        FilterExpressionParser expressionParser = new FilterExpressionParser();
        Expression expression = expressionParser.parseFilterExpression(argMaps);

        // Verify assertions.
        Assert.assertNotNull(expression);
        Assert.assertEquals("((firstName contains ABC) or (lastName equals XYZ))", expression.infix());
    }


    private Map<String, Object> createBinaryFilterArgMap(String left, String op, String right) {
        Map<String, Object> argsMap = new LinkedHashMap<>();
        Map<String, String> nestedMap = new LinkedHashMap<>();
        nestedMap.put(op,right);
        argsMap.put(left,nestedMap);
        return argsMap;
    }

    private Map<String, Object> createCompoundFilterArgMap() {
        Map<String, Object> argsMap = new HashMap<>();
        List<Map> mapList = new ArrayList<>();
        mapList.add(createBinaryFilterArgMap("firstName", "contains", "ABC"));
        mapList.add(createBinaryFilterArgMap("lastName", "equals", "XYZ"));
        argsMap.put("or",mapList);
        return argsMap;
    }
}