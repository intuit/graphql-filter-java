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

/**
 * Base class for a node in the
 * expression tree. All concrete expression nodes
 * will extend from this class.
 *
 * @author sjaiswal
 */
public abstract class AbstractExpression implements Expression {

    private Expression  leftOperand;
    private Operator    operator;
    private Expression  rightOperand;

    /**
     * Default constructor.
     */
    protected AbstractExpression () {
    }

    /**
     * Parameterized constructor.
     * @param leftOperand
     * @param operator
     * @param rightOperand
     */
    protected AbstractExpression(Expression leftOperand, Operator operator, Expression rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    /**
     * Returns the infix string representation of the
     * filter expression ast.
     * @return
     */
    public String infix() {
        StringBuilder expressionBuilder = new StringBuilder();
        expressionBuilder.append("(")
                .append(getLeftOperand().infix())
                .append(" ")
                .append(getOperator().getName())
                .append(" ")
                .append(getRightOperand().infix())
                .append(")");
        return expressionBuilder.toString();
    }

    /**
     * Returns the left operand.
     * @return
     */
    public Expression getLeftOperand() {
        return leftOperand;
    }

    /**
     * Returns the operator.
     * @return
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Returns the right operand.
     * @return
     */
    public Expression getRightOperand() {
        return rightOperand;
    }

    /**
     * Sets the left operand.
     * @param leftOperand
     */
    public void setLeftOperand(Expression leftOperand) {
        this.leftOperand = leftOperand;
    }

    /**
     * Sets the operator.
     * @param operator
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * Sets the right operand.
     * @param rightOperand
     */
    public void setRightOperand(Expression rightOperand) {
        this.rightOperand = rightOperand;
    }
}
