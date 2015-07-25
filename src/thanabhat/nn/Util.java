package thanabhat.nn;

import java.util.Random;

public class Util {

	public static double uniform(double min, double max, Random rng) {
		return rng.nextDouble() * (max - min) + min;
	}

	public static double sigmoid(double x) {
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}

	public static double invSigmoid(double x) {
		return Math.log(x / (1 - x));
	}
}
