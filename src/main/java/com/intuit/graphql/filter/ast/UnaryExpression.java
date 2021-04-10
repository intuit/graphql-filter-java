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
package com.intuit.graphql.filter.ast;

import com.intuit.graphql.filter.visitors.ExpressionVisitor;

/**
 * This class represents an UnaryExpression node
 * in the expression tree with just NOT operator
 * and a single operand which can either be a
 * BinaryExpression or CompoundExpression.
 *
 * @author sjaiswal
 */
public class UnaryExpression extends AbstractExpression{

    /**
     * Default constructor.
     */
    public UnaryExpression() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param leftOperand
     * @param operator
     * @param rightOperand
     */
    public UnaryExpression(Expression leftOperand, Operator operator, Expression rightOperand) {
        super(leftOperand, operator, rightOperand);
    }

    /**
     * This method accepts a expression visitor and calls
     * the visit method on the visitor passing itself.
     *
     * @param visitor ExpressionVisitor for traversing the expression tree.
     * @param data    Buffer to hold the result of process node.
     * @return Returns the processed data.
     */
    @Override
    public <T> T accept(ExpressionVisitor visitor, T data) {
        return (T)visitor.visitUnaryExpression(this, data);
    }
}
