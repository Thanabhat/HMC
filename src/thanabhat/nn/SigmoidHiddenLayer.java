package thanabhat.nn;

import java.util.Random;

public class SigmoidHiddenLayer extends AbstractHiddenLayer {

	public SigmoidHiddenLayer(int nIn, int nOut, double[][] w, double[] b,
			Random rng) {
		super(nIn, nOut, w, b, rng);
	}

	@Override
	protected void activationFunction(double[][] y, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < nOut; j++) {
				y[i][j] = Util.sigmoid(y[i][j]);
			}
		}
	}

	@Override
	public void train(double[] d, double[] dPrev, double lr, double lrReg) {

	}

}
