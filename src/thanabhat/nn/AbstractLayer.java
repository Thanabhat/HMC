package thanabhat.nn;

import java.util.Random;

public abstract class AbstractLayer {
	public int nIn, nOut;
	public double[][] w;
	public double[] b;
	protected Random rng;

	public AbstractLayer(int nIn, int nOut, double[][] w, double[] b, Random rng) {
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

	protected void initWeightW(double[][] w, Random rng){
		for (int i = 0; i < nOut; i++) {
			for (int j = 0; j < nIn; j++) {
				w[i][j] = Util.uniform(-1, 1, rng);
			}
		}
	}

	protected void initWeightB(double[] b, Random rng){
		for (int i = 0; i < nOut; i++) {
			b[i] = Util.uniform(-1, 1, rng);
		}
	}

	protected void calculateMultiplication(double[][] x, double[][] y, int n) {
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < nOut; i++) {
				y[k][i] = 0;
				for (int j = 0; j < nIn; j++) {
					y[k][i] += w[i][j] * x[k][j];
				}
				y[k][i] += b[i];
			}
		}
	}

	public void predict(double[][] x, double[][] y, int n) {
		calculateMultiplication(x, y, n);
		activationFunction(y, n);
	}

	abstract protected void activationFunction(double[][] y, int n);
}
