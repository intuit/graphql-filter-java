package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.ast.BinaryExpression;
import com.intuit.graphql.filter.ast.CompoundExpression;
import com.intuit.graphql.filter.ast.ExpressionField;
import com.intuit.graphql.filter.ast.ExpressionValue;
import com.intuit.graphql.filter.ast.Expression;
/**
 * Interface for traversing the expression
 * tree using visitor design pattern.
 * @param <T>
 *
 *                        AND (Compound Expression)
 *                     /       \
 *                    /         \
 *               CONTAINS        EQ (Binary Expression)
 *               /    \         /   \
 *              /      \       /     \
 *        firstName  Saurabh lastName Jaiswal
 */
public interface ExpressionVisitor<T> {

    /**
     * Returns the final transformed expression.
     * @return
     * @param expression
     */
    public T expression(Expression expression);

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
    public T visitCompoundExpression(CompoundExpression compoundExpression, T data);

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
    public T visitBinaryExpression(BinaryExpression binaryExpression, T data);

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
    public T visitExpressionField(ExpressionField field, T data);

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
    public T visitExpressionValue(ExpressionValue<? extends Comparable>  value, T data);
}
