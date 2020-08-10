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
