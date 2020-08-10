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

import com.intuit.graphql.filter.ast.BinaryExpression;
import com.intuit.graphql.filter.ast.CompoundExpression;
import com.intuit.graphql.filter.ast.UnaryExpression;
import com.intuit.graphql.filter.ast.Expression;
import com.intuit.graphql.filter.ast.ExpressionField;
import com.intuit.graphql.filter.ast.ExpressionValue;
import com.intuit.graphql.filter.ast.Operator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for traversing
 * the expression tree and generating an
 * SQL WHERE clause from it with correct precedence
 * order marked by parenthesis.
 *
 * @author sjaiswal
 */
public class SQLExpressionVisitor implements ExpressionVisitor<String> {

    private Deque<Operator> operatorStack;
    private Map<String, String> fieldMap;

    public SQLExpressionVisitor(Map<String,String> fieldMap) {
        this.operatorStack = new ArrayDeque<>();
        this.fieldMap = fieldMap;
    }

    /**
     * Returns the SQL WHERE clause string
     * from the expression tree.
     * @return
     * @param expression
     */
    @Override
    public String expression(Expression expression) {
        String expressionString = "WHERE ";
        if (expression != null) {
            expressionString = expression.accept(this, expressionString);
        }
        return expressionString;
    }

    /**
     * Handles the processing of compound
     * expression node.
     * @param compoundExpression
     *          Contains compound expression.
     * @param data
     *          Buffer for storing processed data.
     * @return
     *          Data of processed node.
     */
    @Override
    public String visitCompoundExpression(CompoundExpression compoundExpression, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        expressionBuilder.append("(")
                .append(compoundExpression.getLeftOperand().accept(this, ""))
                .append(" ").append(compoundExpression.getOperator().getName().toUpperCase()).append(" ")
                .append(compoundExpression.getRightOperand().accept(this, ""))
                .append(")");
        return expressionBuilder.toString();

    }

    /**
     * Handles the processing of binary
     * expression node.
     * @param binaryExpression
     *          Contains binary expression.
     * @param data
     *          Buffer for storing processed data.
     * @return
     *          Data of processed node.
     */
    @Override
    public String visitBinaryExpression(BinaryExpression binaryExpression, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        expressionBuilder.append("(")
                .append(binaryExpression.getLeftOperand().accept(this, ""))
                .append(" ").append(resolveOperator(binaryExpression.getOperator())).append(" ");
        operatorStack.push(binaryExpression.getOperator());
        expressionBuilder.append(binaryExpression.getRightOperand().accept(this, ""))
                .append(")");
        return expressionBuilder.toString();
    }

    /**
     * Handles the processing of unary
     * expression node.
     * @param unaryExpression
     *          Contains unary expression.
     * @param data
     *          Buffer for storing processed data.
     * @return
     *          Data of processed node.
     */
    @Override
    public String visitUnaryExpression(UnaryExpression unaryExpression, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        expressionBuilder.append("(")
                .append(" ").append(resolveOperator(unaryExpression.getOperator())).append(" ")
                .append(unaryExpression.getLeftOperand().accept(this, ""))
                .append(")");
        return expressionBuilder.toString();
    }

    /**
     * Handles the processing of expression
     * field node.
     * @param field
     *          Contains expression field.
     * @param data
     *          Buffer for storing processed data.
     * @return
     *          Data of processed node.
     */
    @Override
    public String visitExpressionField(ExpressionField field, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        if (fieldMap != null && fieldMap.get(field.infix()) != null) {
            expressionBuilder.append(fieldMap.get(field.infix()));
        } else {
            expressionBuilder.append(field.infix());
        }
        return expressionBuilder.toString();
    }

    /**
     * Handles the processing of expression
     * value node.
     * @param value
     *          Contains expression value.
     * @param data
     *          Buffer for storing processed data.
     * @return
     *          Data of processed node.
     */
    @Override
    public String visitExpressionValue(ExpressionValue<? extends Comparable> value, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        Operator operator = operatorStack.pop();
        List<? extends Comparable> expressionValues  = value.getValues();

        if (operator == Operator.CONTAINS || operator == Operator.STARTS || operator == Operator.ENDS ) {
            expressionBuilder.append("'").append("%").append(expressionValues.get(0)).append("%").append("'");
        } else if(operator == Operator.BETWEEN)  {
            expressionBuilder.append("'").append(expressionValues.get(0)).append("'")
                    .append(" AND ")
                    .append("'").append(expressionValues.get(1)).append("'");
        } else if (operator == Operator.IN) {
            expressionBuilder.append("(");
            for (int i = 0; i < expressionValues.size(); i++) {
                expressionBuilder.append("'").append(expressionValues.get(i)).append("'");
                if (i < expressionValues.size() - 1) {
                    expressionBuilder.append(", ");
                }
            }
            expressionBuilder.append(")");
        } else {
            expressionBuilder.append("'").append(expressionValues.get(0)).append("'");
        }
        return expressionBuilder.toString();
    }

    private String resolveOperator(Operator operator) {
        String op = "";
        switch (operator) {
            /* Logical operators */
            case AND:
            case OR:
            case NOT:
                op = operator.getName().toUpperCase();
                break;

            /* Relational string operators*/
            case EQUALS:
                op = "=";
                break;
            case CONTAINS:
            case STARTS:
            case ENDS:
                op = "LIKE";
                break;

            /* Relational numeric operators*/
            case LT:
                op = "<";
                break;
            case GT:
                op = ">";
                break;
            case EQ:
                op = "=";
                break;
            case GTE:
                op = ">=";
                break;
            case LTE:
                op = "<=";
                break;

            /* Common operators */
            case IN:
                op = "IN";
                break;
            case BETWEEN:
                op = "BETWEEN";
                break;
        }
        return op;
    }
}
