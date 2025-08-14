package kr.hhplus.be.server.application.port.in.aop;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class SpEL {
    private SpEL() {}
    public static Object eval(String[] paramNames, Object[] args, String expr) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) ctx.setVariable(paramNames[i], args[i]);
        return parser.parseExpression(expr).getValue(ctx, Object.class);
    }
}