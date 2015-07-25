package thanabhat.nn;

import java.util.Random;

public class BasicLayer {
	public int nIn, nOut;
	public double[][] w;
	public double[] b;
	protected Random rng;

	public BasicLayer(int nIn, int nOut, double[][] w, double[] b, Random rng) {
		this.nIn = nIn;
		this.nOut = nOut;
		if (rng == null) {
			this.rng = new Random(1234);
		} else {
			this.rng = rng;
		}

		if (w == null) {
			this.w = new double[this.nOut][this.nIn];
			initWeightW(w, rng);
		} else {
			this.w = w;
		}
		if (b == null) {
			b = new double[this.nOut];
			initWeightB(b, rng);
		} else {
			this.b = b;
		}
	}

	protected void initWeightW(double[][] w, Random rng) {
		for (int i = 0; i < nOut; i++) {
			for (int j = 0; j < nIn; j++) {
				w[i][j] = Util.uniform(-1, 1, rng);
			}
		}
	}

	protected void initWeightB(double[] b, Random rng) {
		for (int i = 0; i < nOut; i++) {
			b[i] = Util.uniform(-1, 1, rng);
		}
	}

	/**
	 * yOut = g(zOut)
	 * 
	 * @param a
	 * @param zOut
	 * @param yOut
	 */
	public void fwdProp(double[] a, double[] zOut, double[] yOut) {
		calcFwdMul(a, zOut);
		actFunc(zOut, yOut);
	}

	protected void calcFwdMul(double[] a, double[] zOut) {
		for (int i = 0; i < nOut; i++) {
			zOut[i] = 0;
			for (int j = 0; j < nIn; j++) {
				zOut[i] += w[i][j] * a[j];
			}
			zOut[i] += b[i];
		}
	}

	protected void actFunc(double[] z, double[] yOut) {
		for (int i = 0; i < nOut; i++) {
			yOut[i] = Util.sigmoid(z[i]);
		}
	}

	public void bckProp(double[] d, double[] zPrev, double[] dOut) {
		calcBckMul(d, dOut);
		double[] gInv = new double[nIn];
		invActFunc(zPrev, gInv);
		for (int i = 0; i < nIn; i++) {
			dOut[i] = dOut[i] * gInv[i];
		}
	}

	protected void calcBckMul(double[] d, double[] xOut) {
		for (int j = 0; j < nIn; j++) {
			xOut[j] = 0;
			for (int i = 0; i < nOut; i++) {
				xOut[j] += w[i][j] * d[i];
			}
		}
	}

	protected void invActFunc(double[] zPrev, double[] out) {
		for (int i = 0; i < nIn; i++) {
			out[i] = Util.invSigmoid(zPrev[i]);
		}
	}

	public void updateWeight(double[] d, double[] a, int m, double lr, double lrReg) {
		for (int i = 0; i < nOut; i++) {
			for (int j = 0; j < nIn; j++) {
				w[i][j] += (lr * a[j] * d[i] + lrReg * w[i][j]) / m;
			}
		}
	}
}
