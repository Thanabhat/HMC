package thanabhat.nn;

import java.util.Random;

public class Util {

	public static double uniform(double min, double max, Random rng) {
		return rng.nextDouble() * (max - min) + min;
	}

	public static double sigmoid(double x) {
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}

	public static double derivativeSigmoid(double x) {
		double s = sigmoid(x);
		return s * (1.0 - s);
	}

	public static double average(double[] x) {
		double avg = 0.0;
		for (int i = 0; i < x.length; i++) {
			avg += x[i];
		}
		avg /= x.length;
		return avg;
	}
}
