package HMC.Evaluator;

import java.util.ArrayList;

public class AUPRC {
	static double eps = 1E-7;

	public static double evaluate(double[][] sortedPRPairOrig) {
		double[][] sortedPRPair = removeDuplicateXValue(sortedPRPairOrig);
		assert(sortedPRPair.length >= 2);
		assert(sortedPRPair[0].length == 2);
		int curPos = 0;
		int n = sortedPRPair.length;
		double res = 0.0;
		for (double p = 0.005; p <= 1.00 + eps; p += 0.01) {
			double r;
			while (curPos < n && sortedPRPair[curPos][0] < p) {
				curPos++;
			}
			if (curPos == 0) {
				r = getInterpolatedValue(p, sortedPRPair[0], sortedPRPair[1]);
			} else if (curPos == n) {
				r = getInterpolatedValue(p, sortedPRPair[n - 2], sortedPRPair[n - 1]);
			} else {
				r = getInterpolatedValue(p, sortedPRPair[curPos - 1], sortedPRPair[curPos]);
			}
			if (r < 0.0) {
				r = 0.0;
			}
			if (r > 1.0) {
				r = 1.0;
			}
			res += 0.01 * r;
		}
		return res;
	}

	public static double evaluate(ArrayList<double[]> sortedPRPair) {
		double[][] arr = sortedPRPair.toArray(new double[sortedPRPair.size()][]);
		return evaluate(arr);
	}

	private static double getInterpolatedValue(double x, double[] p1, double[] p2) {
		return (p2[1] - p1[1]) / (p2[0] - p1[0]) * (x - p1[0]) + p1[1];
	}

	private static double[][] removeDuplicateXValue(double[][] sortedPair) {
		assert(sortedPair[0].length == 2);
		ArrayList<double[]> res = new ArrayList<double[]>();
		for (int i = 0; i < sortedPair.length; i++) {
			if (i == 0 || Math.abs(sortedPair[i][0] - sortedPair[i - 1][0]) > eps) {
				res.add(new double[] { sortedPair[i][0], sortedPair[i][1] });
			}
		}
		return res.toArray(new double[res.size()][]);
	}
}
