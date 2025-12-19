package src;

import java.util.*;

public class ExpressionParser {

    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    static {
        PRECEDENCE.put("+", 1);
        PRECEDENCE.put("-", 1);
        PRECEDENCE.put("*", 2);
        PRECEDENCE.put("/", 2);
        PRECEDENCE.put("^", 3);
    }

    public double evaluate(String expression, double xValue) {
        List<Token> postfix = toPostfix(tokenize(expression));
        return evalPostfix(postfix, xValue);
    }

    private List<Token> tokenize(String expr) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i)=='.'))
                    sb.append(expr.charAt(i++));
                tokens.add(new Token(TokenType.NUMBER, sb.toString()));
            }
            else if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i)))
                    sb.append(expr.charAt(i++));
                String word = sb.toString();
                if (word.equals("x")) tokens.add(new Token(TokenType.VARIABLE, word));
                else if (word.equals("pi")) tokens.add(new Token(TokenType.NUMBER, String.valueOf(MathConstants.PI)));
                else if (word.equals("e")) tokens.add(new Token(TokenType.NUMBER, String.valueOf(MathConstants.E)));
                else tokens.add(new Token(TokenType.FUNCTION, word));
            }
            else if (c == '(') { tokens.add(new Token(TokenType.LEFT_PAREN, "(")); i++; }
            else if (c == ')') { tokens.add(new Token(TokenType.RIGHT_PAREN, ")")); i++; }
            else {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c))); i++;
            }
        }
        return tokens;
    }

    private List<Token> toPostfix(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Stack<Token> stack = new Stack<>();

        for (Token t : tokens) {
            switch (t.type) {
                case NUMBER:
                case VARIABLE:
                    output.add(t); break;
                case FUNCTION:
                    stack.push(t); break;
                case OPERATOR:
                    while (!stack.isEmpty() &&
                           (stack.peek().type == TokenType.OPERATOR || stack.peek().type == TokenType.FUNCTION) &&
                           PRECEDENCE.getOrDefault(stack.peek().value, 4) >= PRECEDENCE.get(t.value))
                        output.add(stack.pop());
                    stack.push(t); break;
                case LEFT_PAREN:
                    stack.push(t); break;
                case RIGHT_PAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LEFT_PAREN)
                        output.add(stack.pop());
                    stack.pop();
                    if (!stack.isEmpty() && stack.peek().type == TokenType.FUNCTION)
                        output.add(stack.pop());
            }
        }
        while (!stack.isEmpty()) output.add(stack.pop());
        return output;
    }

    private double evalPostfix(List<Token> postfix, double xValue) {
        Stack<Double> stack = new Stack<>();
        for (Token t : postfix) {
            switch (t.type) {
                case NUMBER: stack.push(Double.parseDouble(t.value)); break;
                case VARIABLE: stack.push(xValue); break;
                case OPERATOR: {
                    double b = stack.pop();
                    double a = stack.pop();
                    switch (t.value) {
                        case "+": stack.push(a + b); break;
                        case "-": stack.push(a - b); break;
                        case "*": stack.push(a * b); break;
                        case "/": stack.push(a / b); break;
                        case "^": stack.push(Math.pow(a, b)); break;
                    }
                    break;
                }
                case FUNCTION: {
                    double v = stack.pop();
                    switch (t.value) {
                        case "sin": stack.push(Math.sin(v)); break;
                        case "cos": stack.push(Math.cos(v)); break;
                        case "tan": stack.push(Math.tan(v)); break;
                        case "log": stack.push(Math.log10(v)); break;
                        case "ln": stack.push(Math.log(v)); break;
                        case "sqrt": stack.push(Math.sqrt(v)); break;
                        default: throw new IllegalArgumentException("Unknown function: " + t.value);
                    }
                }
            }
        }
        return stack.pop();
    }
}
