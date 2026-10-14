package application;

import java.util.ArrayList;
import java.util.List;

public class CalculationService {

    public List<Double> calculateMoments(List<BeamSpan> spans) {
        int n = spans.size();
        if (n == 0) return new ArrayList<>();
        int m = n - 1;
        double[] a = new double[m];
        double[] b = new double[m];
        double[] c = new double[m];
        double[] d = new double[m];

        for (int i = 0; i < m; i++) {
            BeamSpan left = spans.get(i);
            BeamSpan right = spans.get(i + 1);
            double l1 = left.getLength();
            double i1 = left.getInertia();
            double l2 = right.getLength();
            double i2 = right.getInertia();

            a[i] = (i == 0) ? 0 : l1 / i1;
            b[i] = 2 * (l1 / i1 + l2 / i2);
            c[i] = (i == m - 1) ? 0 : l2 / i2;

            double term1 = calculateSpanTerm(left) / i1;
            double term2 = calculateSpanTerm(right) / i2;
            d[i] = -(term1 + term2);
        }

        double[] momentsArr = solveTridiagonal(a, b, c, d);

        List<Double> full = new ArrayList<>();
        full.add(0.0);
        for (double mom : momentsArr) full.add(mom);
        full.add(0.0);
        return full;
    }

    private double calculateSpanTerm(BeamSpan span) {
        double term = 0;
        double l = span.getLength();
        for (Load load : span.getLoads()) {
            double val = load.getValue();
            double a = load.getPosition();
            switch (load.getType()) {
                case UNIFORM:
                    term += val * l * l * l / 4;
                    break;
                case FORCE:
                    term += val * a * (l * l - a * a) / l;
                    break;
                case MOMENT:
                    term += -val * (3 * a * a - l * l) / l;
                    break;
            }
        }
        return term;
    }

    private double[] solveTridiagonal(double[] a, double[] b, double[] c, double[] d) {
        int n = b.length;
        double[] x = new double[n];
        double[] cp = new double[n];
        double[] dp = new double[n];
        cp[0] = c[0] / b[0];
        dp[0] = d[0] / b[0];
        for (int i = 1; i < n; i++) {
            double den = b[i] - a[i] * cp[i - 1];
            cp[i] = c[i] / den;
            dp[i] = (d[i] - a[i] * dp[i - 1]) / den;
        }
        x[n - 1] = dp[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = dp[i] - cp[i] * x[i + 1];
        }
        return x;
    }

    public List<Double> calculateReactions(List<BeamSpan> spans, List<Double> moments) {
        int n = spans.size();
        List<Double> reactions = new ArrayList<>(n + 1);
        for (int i = 0; i < n + 1; i++) {
            reactions.add(0.0);
        }
        for (int i = 0; i < n; i++) {
            BeamSpan span = spans.get(i);
            double l = span.getLength();
            double mLeft = moments.get(i);
            double mRight = moments.get(i + 1);
            double freeLeft = calculateFreeLeftReaction(span);
            double freeRight = calculateFreeRightReaction(span);
            double adjLeft = freeLeft + (mLeft - mRight) / l;
            double adjRight = freeRight + (mRight - mLeft) / l;
            reactions.set(i, reactions.get(i) + adjLeft);
            reactions.set(i + 1, reactions.get(i + 1) + adjRight);
        }
        return reactions;
    }

    private double calculateFreeLeftReaction(BeamSpan span) {
        double sum = 0;
        double l = span.getLength();
        for (Load load : span.getLoads()) {
            if (load.getType() == Load.LoadType.UNIFORM) {
                sum += load.getValue() * l / 2;
            } else if (load.getType() == Load.LoadType.FORCE) {
                sum += load.getValue() * (l - load.getPosition()) / l;
            }
        }
        return sum;
    }

    private double calculateFreeRightReaction(BeamSpan span) {
        double sum = 0;
        double l = span.getLength();
        for (Load load : span.getLoads()) {
            if (load.getType() == Load.LoadType.UNIFORM) {
                sum += load.getValue() * l / 2;
            } else if (load.getType() == Load.LoadType.FORCE) {
                sum += load.getValue() * load.getPosition() / l;
            }
        }
        return sum;
    }

    public double calculateFreeMoment(BeamSpan span, double x) {
        double m = 0;
        double l = span.getLength();
        for (Load load : span.getLoads()) {
            double val = load.getValue();
            double a = load.getPosition();
            if (load.getType() == Load.LoadType.UNIFORM) {
                m += val * x * (l - x) / 2;
            } else if (load.getType() == Load.LoadType.FORCE) {
                if (x <= a) {
                    m += val * (l - a) * x / l;
                } else {
                    m += val * a * (l - x) / l;
                }
            } else if (load.getType() == Load.LoadType.MOMENT) {
                if (x > a) m += val;
            }
        }
        return m;
    }
}