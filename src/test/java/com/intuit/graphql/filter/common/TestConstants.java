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
package com.intuit.graphql.filter.common;

/**
 * @author sjaiswal
 */
public class TestConstants {

    public static final String NO_FILER = "{\n" +
            "  searchEmployees {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String BINARY_FILER = "{\n" +
            "  searchEmployees (filter : {firstName : {contains: \"Saurabh\"}}) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String BINARY_FILER_INT = "{\n" +
            "  searchEmployees (filter : { age: {gte: 25}}) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";
    public static final String COMPOUND_FILER_WITH_OR = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      or : [{ firstName : {contains : \"Saurabh\"}},{ lastName : {equals : \"Jaiswal\"}}]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String COMPOUND_FILER_WITH_AND = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      and : [{ firstName : {contains : \"Saurabh\"}},{ lastName : {equals : \"Jaiswal\"}}]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String COMPOUND_FILER_WITH_AND_OR = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      and : [\n" +
            "        { firstName : {contains : \"Saurabh\"}},\n" +
            "        { or : [{ lastName: {equals : \"Jaiswal\"}},{ age: {gte: 25}}]}" +
            "       ]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";
    public static final String COMPOUND_FILER_WITH_OR_AND = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      or : [\n" +
            "        { firstName : {contains : \"Saurabh\"}},\n" +
            "        { and : [{ lastName: {equals : \"Jaiswal\"}},{ age: {gte: 25}}\n" +
            "        ]}]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String COMPOUND_FILER_WITH_OR_OR_AND = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      or : [\n" +
            "          { firstName: {contains: \"Saurabh\"}},\n" +
            "          { lastName: {equals: \"Jaiswal\"}}\n" +
            "          { and : [\n" +
            "             { firstName: {equals: \"Vinod\"}},\n" +
            "             { age: {gte: 30}}\n" +
            "          ]}\n" +
            "        ]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String COMPOUND_FILER_WITH_AND_AND_OR = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      and : [\n" +
            "          { firstName: {contains: \"Saurabh\"}},\n" +
            "          { lastName: {equals: \"Jaiswal\"}}\n" +
            "          { or : [\n" +
            "             { firstName: {equals: \"Vinod\"}},\n" +
            "             { age: {gte: 30}}\n" +
            "          ]}\n" +
            "        ]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String COMPOUND_DATE_FILTER = "{\n" +
            "  searchEmployees (filter : {\n" +
            "      or : [{ lastName : {equals : \"Jaiswal\"}}, {birthDate " +
            ":{gt : \"1996-12-19T16:39:57-08:00\"}}]\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String FILTER_WITH_OTHER_ARGS = "{\n" +
            "  searchEmployees (id: \"123\", filter : {\n" +
            "        firstName : {equals: \"Saurabh\"}\n" +
            "    }) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String FILTER_WITH_VARIABLE = "query searchEmployeesWithFilter ($employeeFilter :  " +
            "EmployeeFilter = {\n" +
            "      and : [{ firstName : {contains : \"Saurabh\"}},{ lastName : {equals : \"Jaiswal\"}}]\n" +
            "    }){\n" +
            "  searchEmployees (filter : $employeeFilter) {\n" +
            "      firstName\n" +
            "      lastName\n" +
            "      age\n" +
            "    }\n" +
            "}";

    public static final String LAST_NAME_IN = "{\n" +
            "  searchEmployees(filter: {\n" +
            "    lastName: {in: [\"Jaiswal\",\"Gupta\",\"Kumar\"]}\n" +
            "  })\n" +
            "  {\n" +
            "    firstName\n" +
            "    lastName\n" +
            "  }\n" +
            "}";

    public static final String AGE_IN = "{\n" +
            "  searchEmployees(filter: {\n" +
            "    age: {in: [32, 35, 40]}\n" +
            "  })\n" +
            "  {\n" +
            "    firstName\n" +
            "    lastName\n" +
            "  }\n" +
            "}";

    public static final String INVALID_FILTER = "{\n" +
            "  searchEmployees(filter: {\n" +
            "    firstName : {contains: \"Saurabh\"}\n" +
            "    or: [\n" +
            "      {lastName: {contains: \"Smith\"}},\n" +
            "      {age: {lte: 30}}\n" +
            "    ]\n" +
            "  }) {\n" +
            "    firstName\n" +
            "    lastName\n" +
            "    age\n" +
            "  }\n" +
            "}";

    public static final String NOT_FILTER = "{\n" +
            "  searchEmployees(filter: {\n" +
            "      not : { firstName : {equals : \"Saurabh\"} }\n" +
            "  }) {\n" +
            "    firstName\n" +
            "    lastName\n" +
            "  }\n" +
            "}";

    public static final String NOT_COMPOUND_FILTER = "{\n" +
            "  searchEmployees(filter: {\n" +
            "      not : { and : [\n" +
            "        {firstName : {equals: \"Saurabh\"}},\n" +
            "        {lastName: {contains: \"Jaiswal\"}}\n" +
            "      ]}\n" +
            "  }) {\n" +
            "    firstName\n" +
            "    lastName\n" +
            "  }\n" +
            "}";

    public static final String COMPOUND_NOT_FILTER = "{\n" +
            "  searchEmployees(filter: {\n" +
            "      and : [\n" +
            "        {firstName : {equals: \"Saurabh\"}},\n" +
            "        { not: {lastName: {contains: \"Jaiswal\"}}}\n" +
            "      ]\n" +
            "  }) {\n" +
            "    firstName\n" +
            "    lastName\n" +
            "  }\n" +
            "}";
}
