package com.intuit.graphql.filter.visitors;

import com.intuit.graphql.filter.ast.BinaryExpression;
import com.intuit.graphql.filter.ast.CompoundExpression;
import com.intuit.graphql.filter.ast.Expression;
import com.intuit.graphql.filter.ast.ExpressionField;
import com.intuit.graphql.filter.ast.ExpressionValue;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Map;

/**
 * This class is responsible for traversing
 * the expression tree and generating a compound
 * JPA Specification from it with correct precedence
 * order.
 */
public class JpaSpecificationExpressionVisitor<T> implements ExpressionVisitor<Specification<T>>{

    private Map<String, String> fieldMap;

    public JpaSpecificationExpressionVisitor(Map<String, String> fieldMap) {
        this.fieldMap = fieldMap;
    }

    /**
     * Returns the JPA Specification from
     * the expression tree.
     * @return
     * @param expression
     */
    @Override
    public Specification<T> expression(Expression expression) {
        Specification<T> specification = null;
        if (expression != null){
            specification = expression.accept(this, null);
        }
        return specification;
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
    public Specification<T> visitCompoundExpression(CompoundExpression compoundExpression, Specification<T> data) {
        Specification<T> result = null;
        switch (compoundExpression.getOperator()) {
            /* Logical operations.*/
            case AND:
                Specification<T> left = compoundExpression.getLeftOperand().accept(this, null);
                Specification<T> right = compoundExpression.getRightOperand().accept(this, null);
                result = Specification.where(left).and(right);

                break;

            case OR:
                left = compoundExpression.getLeftOperand().accept(this, null);
                right = compoundExpression.getRightOperand().accept(this, null);
                result = Specification.where(left).or(right);
                break;

            case NOT:
                break;
        }
        return result;
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
    public Specification<T> visitBinaryExpression(BinaryExpression binaryExpression, Specification<T> data) {

        Specification<T> specification = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                                                                            criteriaBuilder) {

                ExpressionValue<? extends Comparable> operandValue = (ExpressionValue<? extends Comparable>)binaryExpression.getRightOperand();
                Predicate predicate = null;
                String fieldName = mappedFieldName(binaryExpression.getLeftOperand().infix());
                Path path = root.get(fieldName);

                switch (binaryExpression.getOperator()) {
                    /* String operations.*/
                    case STARTS:
                    case ENDS:
                    case CONTAINS:
                        predicate = criteriaBuilder.like(path, "%" + operandValue.value() + "%");
                        break;

                    case EQUALS:
                        predicate = criteriaBuilder.equal(path,  operandValue.value());
                        break;

                    /* Numeric operations.*/
                    case LT:
                        predicate = criteriaBuilder.lessThan(path, operandValue.value());
                        break;

                    case LTE:
                        predicate = criteriaBuilder.lessThanOrEqualTo(path, operandValue.value());
                        break;

                    case EQ:
                        predicate = criteriaBuilder.equal(path, operandValue.value());
                        break;

                    case GT:
                        predicate = criteriaBuilder.greaterThan(path, operandValue.value());
                        break;

                    case GTE:
                        predicate = criteriaBuilder.greaterThanOrEqualTo(path, operandValue.value());
                        break;

                    case IN:
                        predicate = criteriaBuilder.in(path).value(operandValue.getValues());
                        break;

                    case BETWEEN:
                        predicate = criteriaBuilder.between(path,operandValue.getValues().get(0),operandValue.getValues().get(1));
                        break;
                }
                return predicate;
            }
        };
        return specification;
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
    public Specification<T> visitExpressionField(ExpressionField field, Specification<T> data) {
        /* ExpressionField has been taken care in the Binary expression visitor. */
        return null;
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
    public Specification<T> visitExpressionValue(ExpressionValue<? extends Comparable> value, Specification<T> data) {
        /* ExpressionValue has been taken care in the Binary expression visitor. */
        return null;
    }

    private String mappedFieldName(String fieldName) {
        StringBuilder expressionBuilder = new StringBuilder();
        if (fieldMap != null && fieldMap.get(fieldName) != null) {
            expressionBuilder.append(fieldMap.get(fieldName));
        } else {
            expressionBuilder.append(fieldName);
        }
        return expressionBuilder.toString();
    }
}
