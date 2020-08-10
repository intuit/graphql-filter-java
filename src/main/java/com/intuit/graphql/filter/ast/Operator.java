package com.intuit.graphql.filter.ast;

/**
 * Enum of operators for supporting relational
 * and logical expressions.
 */
public enum Operator {

    /* Logical Operators */
    AND("and", "Logical",  Kind.LOGICAL),
    OR ("or", "Logical",  Kind.LOGICAL),
    NOT ("not", "Logical", Kind.LOGICAL),

    /* String Operators */
    EQUALS("equals", "String", Kind.RELATIONAL),
    CONTAINS("contains", "String",  Kind.RELATIONAL),
    STARTS("starts", "String",  Kind.RELATIONAL),
    ENDS("ends", "String",  Kind.RELATIONAL),

    /* Numeric Operators */
    EQ("eq", "Numeric", Kind.RELATIONAL),
    GT("gt", "Numeric",  Kind.RELATIONAL),
    GTE("gte", "Numeric",  Kind.RELATIONAL),
    LT("lt", "Numeric",  Kind.RELATIONAL),
    LTE("lte", "Numeric",  Kind.RELATIONAL),

    /* Range Operators */
    IN("in", "String|Numeric",  Kind.RELATIONAL),
    BETWEEN("between","DateTime|Numeric", Kind.RELATIONAL);

    /**
     * Enum of operator kind.
     */
    enum Kind {
        LOGICAL,
        RELATIONAL;
    }

    private String name;
    private String type;
    private Kind kind;

    Operator(String name, String type, Kind kind) {
        this.type = type;
        this.name = name;
        this.kind = kind;
    }

    /**
     * Returns the Operator enum based
     * on operator name.
     * @param name
     * @return
     */
    public static Operator getOperator(String name) {
        for (Operator operator : Operator.values()) {
            if (operator.getName().equals(name)) {
                return operator;
            }
        }
        throw new IllegalArgumentException(String.valueOf(name));
    }

    /**
     * Returns Operator kind based
     * on operator name.
     * @param name
     * @return
     */
    public static String getOperatorKind(String name) {
        return getOperator(name).getKind().name();
    }

    /**
     * Returns operator name.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns operator kind.
     * @return
     */
    public Kind getKind() {
        return kind;
    }
}
