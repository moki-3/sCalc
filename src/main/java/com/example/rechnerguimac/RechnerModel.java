package com.example.rechnerguimac;

//Backend

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class RechnerModel {

    // ÷ durch / ersetzen !!!
    // × durch * ersetzen !!!

    public resultType calculate(String expr) {


        expr = expr.replace(",", ".");
        expr = expr.replace("×", "*");
        expr = expr.replace("÷", "/");

        double result = 0;
        try {
            Expression expression = new ExpressionBuilder(expr).build();
            result = expression.evaluate();
        } catch (Exception e) {
            return new resultType(-1, true);
        }

        return new resultType(result, false);

    }
}
