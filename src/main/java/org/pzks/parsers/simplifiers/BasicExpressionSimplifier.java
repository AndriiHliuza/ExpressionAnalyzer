package org.pzks.parsers.simplifiers;


public class BasicExpressionSimplifier {
    private String expression;

    public BasicExpressionSimplifier(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public BasicExpressionSimplifier simplifyOnes() {
        expression = expression.replaceAll("\\*(1|\\+1|1\\.1|\\+1\\.1)", "");         // *1
        expression = expression.replaceAll("/(1|\\+1|1\\.1|\\+1\\.1)", "");           // /1

        expression = expression.replaceAll("(?<=[+\\-*])(1|\\+1|1\\.1|\\+1\\.1)\\*", "");   // 1*
        expression = expression.replaceAll("^(1|\\+1|1\\.1|\\+1\\.1)\\*", "");

        expression = expression.replaceAll("(?<=[+\\-*])(-1|-1\\.1)\\*", "-");   // 1*
        expression = expression.replaceAll("^(-1|-1\\.1)\\*", "-");
        return this;
    }

    public BasicExpressionSimplifier simplifyZeros() {
        // operations with 0: 0 * or 0 /
        expression = expression.replaceAll("(?<=[+\\-*])(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+", "0");                   // [+-*]0*/variable or 0*number
        expression = expression.replaceAll("(?<=[+\\-*])(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\(\\)", "0");             // [+-*]0*/func()
        expression = expression.replaceAll("(?<=[+\\-*])(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\((\\w+,)*\\w+\\)", "0"); // [+-*]0*/func(5) or 0*/func(a) or 0*/func(a,4,b)

        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+", "0");
        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\(\\)", "0");
        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\((\\w+,)*\\w+\\)", "0");


        // operations with 0: * 0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");                    // [+-*]variable*0 or number*0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\(\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");              // [+-*]func()*0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\((\\w+,)*\\w+\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");  // [+-*]func(5)*0 or func(a)*0 or func(a,4,b)*0

        expression = expression.replaceAll("^\\w+\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");
        expression = expression.replaceAll("^\\w+\\(\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");
        expression = expression.replaceAll("^\\w+\\((\\w+,)*\\w+\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");

        // just zero with + or -
        expression = expression.replaceAll("^(-0|\\+0|-0\\.0|\\+0\\.0)$", "0");

        // operations with 0: 0+ or 0- or +0 or -0
        expression = expression.replaceAll("[+\\-](0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?=[+\\-])", "");
        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)[+\\-]", "");
        expression = expression.replaceAll("[+\\-](0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)$", "");
        return this;
    }

    public BasicExpressionSimplifier removeUnnecessaryZerosAfterDotInNumbers() {
        expression = expression.replaceAll("(\\d+)\\.0+", "$1");
        return this;
    }
}
