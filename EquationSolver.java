package src;

public class EquationSolver {
    private ExpressionParser parser = new ExpressionParser();

    public double solve(String left, String right, double a, double b) {
        for (int i = 0; i < 100; i++) {
            double mid = (a + b) / 2;
            double fA = parser.evaluate(left, a) - parser.evaluate(right, a);
            double fM = parser.evaluate(left, mid) - parser.evaluate(right, mid);
            if (fA * fM <= 0) b = mid;
            else a = mid;
        }
        return (a + b) / 2;
    }
}
