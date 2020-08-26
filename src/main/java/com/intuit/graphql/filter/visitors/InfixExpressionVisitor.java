package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.ast.BinaryExpression;
import com.intuit.graphql.filter.ast.CompoundExpression;
import com.intuit.graphql.filter.ast.UnaryExpression;
import com.intuit.graphql.filter.ast.Expression;
import com.intuit.graphql.filter.ast.ExpressionField;
import com.intuit.graphql.filter.ast.ExpressionValue;
import java.util.Map;

/**
 * This class is responsible for traversing
 * the expression tree and generating an
 * infix version of it with correct precedence
 * order marked by parenthesis.
 */
public class InfixExpressionVisitor implements ExpressionVisitor<String> {

    private Map<String, String> fieldMap;

    public InfixExpressionVisitor(Map<String,String> fieldMap) {
        this.fieldMap = fieldMap;
    }

    /**
     * Returns the infix string version
     * of the expression tree.
     * @return
     * @param expression
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
                        .append(" ").append(compoundExpression.getOperator().getName()).append(" ")
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
                        .append(" ").append(binaryExpression.getOperator().getName()).append(" ")
                        .append(binaryExpression.getRightOperand().accept(this, ""))
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
                .append(" ").append(unaryExpression.getOperator()).append(" ")
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
        expressionBuilder.append(value.value());
        return expressionBuilder.toString();
    }
}
