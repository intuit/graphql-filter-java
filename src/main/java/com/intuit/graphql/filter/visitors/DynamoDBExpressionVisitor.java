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

import com.intuit.graphql.filter.ast.BinaryExpression;
import com.intuit.graphql.filter.ast.CompoundExpression;
import com.intuit.graphql.filter.ast.Expression;
import com.intuit.graphql.filter.ast.ExpressionField;
import com.intuit.graphql.filter.ast.ExpressionValue;
import com.intuit.graphql.filter.ast.Operator;
import com.intuit.graphql.filter.ast.UnaryExpression;
import com.intuit.graphql.filter.client.FieldValuePair;
import com.intuit.graphql.filter.client.FieldValueTransformer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for traversing
 * the expression tree and generating a
 * DynamoDB filter expression string from it with correct
 * precedence order marked by parentheses.
 *
 * <p>The visitor produces two outputs:
 * <ul>
 *   <li>A filter expression string (e.g. {@code (contains(firstName, :firstName))})
 *       suitable for use as a DynamoDB {@code FilterExpression} or
 *       {@code KeyConditionExpression}.</li>
 *   <li>An expression-values map (e.g. {@code {":firstName": "Saurabh"}}) suitable
 *       for use as {@code ExpressionAttributeValues} after converting each value to
 *       an {@code AttributeValue}.</li>
 * </ul>
 *
 * @author sjaiswal
 */
public class DynamoDBExpressionVisitor implements ExpressionVisitor<String> {

    private final Deque<Operator> operatorStack;
    private final Map<String, String> fieldMap;
    private final Deque<ExpressionField> fieldStack;
    private final FieldValueTransformer fieldValueTransformer;
    private final Map<String, Object> expressionValues;
    private final Map<String, String> expressionNames;

    public DynamoDBExpressionVisitor(Map<String, String> fieldMap,
                                     FieldValueTransformer fieldValueTransformer) {
        this.operatorStack = new ArrayDeque<>();
        this.fieldMap = fieldMap;
        this.fieldStack = new ArrayDeque<>();
        this.fieldValueTransformer = fieldValueTransformer;
        this.expressionValues = new HashMap<>();
        this.expressionNames = new HashMap<>();
    }

    /**
     * Entry point — initiates AST traversal and returns the complete
     * DynamoDB filter expression string.
     */
    @Override
    public String expression(Expression expression) {
        String expressionString = "";
        if (expression != null) {
            expressionString = expression.accept(this, expressionString);
        }
        return expressionString;
    }

    /**
     * Handles AND / OR compound expressions.
     * Wraps both operands in parentheses to preserve precedence.
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
     * Handles binary field-value comparisons.
     * CONTAINS and STARTS use DynamoDB function syntax and are handled
     * specially in {@link #visitExpressionValue}.
     */
    @Override
    public String visitBinaryExpression(BinaryExpression binaryExpression, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        Operator operator = binaryExpression.getOperator();

        if (operator == Operator.STARTS || operator == Operator.CONTAINS) {
            // Function-style operators: field name is embedded in visitExpressionValue
            operatorStack.push(operator);
            binaryExpression.getLeftOperand().accept(this, "");
            expressionBuilder.append(binaryExpression.getRightOperand().accept(this, ""));
        } else {
            expressionBuilder.append("(")
                    .append(binaryExpression.getLeftOperand().accept(this, ""))
                    .append(" ").append(resolveOperator(operator)).append(" ");
            operatorStack.push(operator);
            expressionBuilder.append(binaryExpression.getRightOperand().accept(this, ""))
                    .append(")");
        }
        return expressionBuilder.toString();
    }

    /**
     * Handles NOT unary expressions.
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
     * Handles field name resolution including fieldMap and custom transformer.
     * For STARTS/CONTAINS the field name is not appended here — it is used
     * inside {@link #visitExpressionValue} to build the function call.
     */
    @Override
    public String visitExpressionField(ExpressionField field, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        String fieldName = field.infix();

        if (fieldMap != null && fieldMap.get(fieldName) != null) {
            fieldName = fieldMap.get(fieldName);
        } else if (fieldValueTransformer != null
                && fieldValueTransformer.transformField(fieldName) != null) {
            fieldName = fieldValueTransformer.transformField(fieldName);
        }
        fieldStack.push(field);

        Operator operator = operatorStack.peek();
        if (operator == Operator.STARTS || operator == Operator.CONTAINS) {
            // Field name is written inside visitExpressionValue for function calls
            return expressionBuilder.toString();
        }
        expressionBuilder.append(fieldName);
        return expressionBuilder.toString();
    }

    /**
     * Handles value nodes — generates the expression-attribute placeholder name,
     * stores the value in {@link #expressionValues}, and builds the final expression
     * fragment for the current operator.
     */
    @Override
    public String visitExpressionValue(ExpressionValue<? extends Comparable> value, String data) {
        StringBuilder expressionBuilder = new StringBuilder(data);
        Operator operator = operatorStack.pop();
        ExpressionField field = fieldStack.pop();
        String fieldName = resolveFieldName(field.infix());

        if (!fieldStack.isEmpty() && fieldValueTransformer != null) {
            FieldValuePair fieldValuePair = fieldValueTransformer.transformValue(field.infix(), value.value());
            if (fieldValuePair != null && fieldValuePair.getValue() != null) {
                value = new ExpressionValue(fieldValuePair.getValue());
            }
        }

        String expressionValue = deriveValueParameterName(operator, fieldName);
        expressionValues.put(expressionValue, value.value());

        if (operator == Operator.STARTS) {
            expressionBuilder.append("(begins_with(")
                    .append(fieldName).append(", ").append(expressionValue).append("))");
        } else if (operator == Operator.CONTAINS) {
            expressionBuilder.append("(contains(")
                    .append(fieldName).append(", ").append(expressionValue).append("))");
        } else if (operator == Operator.BETWEEN) {
            List<Comparable> values = (List<Comparable>) value.value();
            String minValue = ":min_" + fieldName;
            String maxValue = ":max_" + fieldName;
            this.expressionValues.put(minValue, values.get(0));
            this.expressionValues.put(maxValue, values.get(1));
            expressionBuilder.append(minValue).append(" AND ").append(maxValue);
        } else if (operator == Operator.IN) {
            List<Comparable> values = (List<Comparable>) value.value();
            expressionBuilder.append("(");
            for (int i = 0; i < values.size(); i++) {
                String inValue = ":" + fieldName + "_" + i;
                this.expressionValues.put(inValue, values.get(i));
                expressionBuilder.append(inValue);
                if (i < values.size() - 1) {
                    expressionBuilder.append(", ");
                }
            }
            expressionBuilder.append(")");
        } else {
            expressionBuilder.append(expressionValue);
        }
        return expressionBuilder.toString();
    }

    /**
     * Returns the expression-attribute values map, e.g.
     * {@code {":firstName": "Saurabh", ":min_age": 25}}.
     * Use this to populate {@code ExpressionAttributeValues} on the DynamoDB request
     * after converting each value to an {@code AttributeValue}.
     */
    public Map<String, Object> getExpressionValues() {
        return expressionValues;
    }

    /**
     * Returns the expression-attribute names map for aliasing reserved words.
     * Currently unused but available for callers that need {@code ExpressionAttributeNames}.
     */
    public Map<String, String> getExpressionNames() {
        return expressionNames;
    }

    private String resolveFieldName(String fieldName) {
        if (fieldMap != null && fieldMap.get(fieldName) != null) {
            return fieldMap.get(fieldName);
        }
        if (fieldValueTransformer != null
                && fieldValueTransformer.transformField(fieldName) != null) {
            return fieldValueTransformer.transformField(fieldName);
        }
        return fieldName;
    }

    /**
     * Derives the placeholder parameter name for a given operator and field.
     * Range-lower operators use {@code :min_field}; range-upper operators use
     * {@code :max_field}; all others use {@code :field}.
     */
    private String deriveValueParameterName(Operator operator, String fieldName) {
        switch (operator) {
            case GT:
            case AFTER:
            case GTE:
            case ON_OR_AFTER:
                return ":min_" + fieldName;
            case LT:
            case BEFORE:
            case LTE:
            case ON_OR_BEFORE:
                return ":max_" + fieldName;
            default:
                return ":" + fieldName;
        }
    }

    private String resolveOperator(Operator operator) {
        switch (operator) {
            case AND:
            case OR:
            case NOT:
                return operator.getName().toUpperCase();
            case CONTAINS:
                return "contains";
            case STARTS:
                return "begins_with";
            case LT:
            case BEFORE:
                return "<";
            case GT:
            case AFTER:
                return ">";
            case EQ:
            case EQUALS:
            case ON:
                return "=";
            case GTE:
            case ON_OR_AFTER:
                return ">=";
            case LTE:
            case ON_OR_BEFORE:
                return "<=";
            case IN:
                return "IN";
            case BETWEEN:
                return "BETWEEN";
            default:
                return "";
        }
    }
}
