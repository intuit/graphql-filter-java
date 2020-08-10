package com.intuit.graphql.filter.client;

import com.intuit.graphql.filter.ast.Expression;
import com.intuit.graphql.filter.visitors.ExpressionVisitor;
import graphql.language.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by clients of this
 * library to get the target filter expression
 * of specified format.
 */
public class FilterExpression {

    private Field field;
    private Map<String,String> fieldMap;
    private Expression expressionAst;

    private FilterExpression(FilterExpressionBuilder expressionBuilder) {
        this.field = expressionBuilder.field;
        this.fieldMap = expressionBuilder.fieldMap;
        this.expressionAst = expressionBuilder.expressionAst;
    }

    /**
     * Builder class responsible for building the
     * instance of FilterExpression class.
     */
    public static class FilterExpressionBuilder {

        private Field field;
        private Map<String,String> fieldMap;
        private Expression expressionAst;
        private Map args;
        private final String FILTER_ARG = "filter";

        private FilterExpressionBuilder () {
            fieldMap = new HashMap<>();
        }

        public FilterExpressionBuilder field(Field field) {
            this.field = field;
            return this;
        }

        public FilterExpressionBuilder map(String source, String target) {
            fieldMap.put(source,target);
            return this;
        }

        public FilterExpressionBuilder map(Map<String, String> fieldMap) {
            this.fieldMap = fieldMap;
            return this;
        }

        public FilterExpressionBuilder args(Map filterArgs) {
            this.args = filterArgs;
            return this;
        }

        public FilterExpression build() {
            FilterExpressionParser expressionParser = new FilterExpressionParser();
            if (args != null) {
                Object filter = args.get(FILTER_ARG);
                if (filter != null) {
                    expressionAst = expressionParser.parseFilterExpression((Map) filter);
                }
            }
            FilterExpression expression = new FilterExpression(this);
            return expression;
        }
    }

    public static FilterExpressionBuilder newFilterExpressionBuilder() {
        return new FilterExpressionBuilder();
    }

    /**
     * This method returns the expression in
     * required format.
     * @param format
     * @param <T>
     * @return
     */
    public <T> T getExpression(ExpressionFormat format) {
        if (expressionAst == null) {
            throw new InvalidFilterException("Missing or invalid filter arguments");
        }
        ExpressionVisitor<T> expressionVisitor = ExpressionVisitorFactory.getExpressionVisitor(format, fieldMap);
        return expressionVisitor.expression(expressionAst);
    }
}
