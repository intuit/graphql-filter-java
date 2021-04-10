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

import com.intuit.graphql.filter.visitors.ExpressionVisitor;
import com.intuit.graphql.filter.visitors.InfixExpressionVisitor;
import com.intuit.graphql.filter.visitors.JpaSpecificationExpressionVisitor;
import com.intuit.graphql.filter.visitors.SQLExpressionVisitor;

import java.util.Map;

/**
 * Expression visitor factory responsible
 * for creating instances of supported
 * expression visitors.
 *
 * @author sjaiswal
 */
class ExpressionVisitorFactory {

    /**
     * Factory method for creating and returning
     * instances of ExpressionVisitor.
     * @param format
     * @param fieldMap
     * @return
     */
    static ExpressionVisitor getExpressionVisitor(ExpressionFormat format,
                                                         Map<String, String> fieldMap) {
        ExpressionVisitor expressionVisitor = new InfixExpressionVisitor(fieldMap);
        if (format != null) {
            switch (format) {
                case INFIX:
                    expressionVisitor = new InfixExpressionVisitor(fieldMap);
                    break;
                case SQL:
                    expressionVisitor =  new SQLExpressionVisitor(fieldMap);
                    break;
                case JPA:
                    expressionVisitor = new JpaSpecificationExpressionVisitor(fieldMap);
            }
        }
        return expressionVisitor;
    }
}
