package com.intuit.graphql.filter.client;

import com.intuit.graphql.filter.ast.*;

import java.time.*;
import java.util.*;

/**
 * GraphQL Filter Expression Parser.
 */
class FilterExpressionParser {

    /**
     * Parses the given graphql filter expression AST.
     * @param filterArgs
     * @return
     */
    public Expression parseFilterExpression(Map filterArgs) {
        return createExpressionTree(filterArgs);
    }

    private Expression createExpressionTree(Map filterMap) {
        if (filterMap == null || filterMap.isEmpty() || filterMap.size() > 1) {
            return null;
        }
        Deque<Expression> expressionStack = new ArrayDeque<>();
        Expression expression = null;
        Set<Map.Entry> entries =  filterMap.entrySet();
        for (Map.Entry entry : entries) {
            String key = entry.getKey().toString();
            if (isOperator(key)) {
                String kind = Operator.getOperatorKind(key);
                switch (kind) {

                    /* Case to handle the compound expression.*/
                    case "LOGICAL":
                        List values = (List)entry.getValue();
                        for (Object o : values) {
                            Expression operand = createExpressionTree((Map)o);
                            expressionStack.push(operand);
                            Expression right = expressionStack.pop();
                            Expression left = expressionStack.peek();
                            if (validateExpression(right) && validateExpression(left)) {
                                left = expressionStack.pop();
                                Expression newExp = new CompoundExpression(left, Operator.getOperator(key), right);
                                expressionStack.push(newExp);
                            } else {
                                expressionStack.push(right);
                            }
                        }
                        expression = expressionStack.pop();
                        break;

                    /* Case to handle the binary expression.*/
                    case "RELATIONAL":
                        BinaryExpression binaryExpression = new BinaryExpression();
                        binaryExpression.setOperator(Operator.getOperator(key));
                        List<Comparable> expressionValues = new ArrayList<>();
                        if (entry.getValue() instanceof Collection) {
                            List<Comparable> operandValues = (List<Comparable>) entry.getValue();
                            for (Comparable value : operandValues) {
                                expressionValues.add(convertIfDate(value));
                            }
                        } else {
                            expressionValues.add(convertIfDate((Comparable) entry.getValue()));
                        }
                        ExpressionValue<Comparable> expressionValue = new ExpressionValue(expressionValues);
                        binaryExpression.setRightOperand(expressionValue);
                        expression = binaryExpression;
                        break;
                }
            } else {
                /* Case to handle the Field expression.*/
                ExpressionField leftOperand = new ExpressionField(entry.getKey().toString());
                BinaryExpression binaryExpression = (BinaryExpression) createExpressionTree((Map)entry.getValue());
                binaryExpression.setLeftOperand(leftOperand);
                expression = binaryExpression;
            }
        }

        return expression;
    }

    private boolean isOperator(String key) {
        Operator operator = null;
        try {
            operator = Operator.getOperator(key);
        } catch (Exception ex) {

        }
        return operator == null ? false : true;
    }

    private Comparable convertIfDate(Comparable value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            LocalDate localDate = (LocalDate) value;
            value = java.util.Date.from(localDate.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
        } else if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            value = java.util.Date
                    .from(localDateTime.atZone(ZoneId.systemDefault())
                            .toInstant());
        } else if (value instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) value;
            value = java.util.Date
                    .from(offsetDateTime.toInstant());
        }
        return value;
    }

    /**
     * Validates if the given expression is
     * instance of Binary or Compound expression.
     * @param expression
     * @return
     */
    private boolean validateExpression(Expression expression) {
        if( expression != null && (expression instanceof BinaryExpression || expression instanceof CompoundExpression)) {
            return true;
        }
        return false;
    }

}

