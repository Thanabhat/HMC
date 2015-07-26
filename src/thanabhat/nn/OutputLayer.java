package thanabhat.nn;

import java.util.Random;

public class OutputLayer extends BasicLayer {

	public OutputLayer(int nIn, int nOut, double[][] w, double[] b, Random rng) {
		super(nIn, nOut, w, b, rng);
	}

	public void calcD(double[] diff, double[] z, double[] dOut) {
		for (int i = 0; i < nOut; i++) {
			dOut[i] = diff[i];
		}
		double[] gInv = new double[nOut];
		dActFunc(z, gInv);
		for (int i = 0; i < nOut; i++) {
			dOut[i] = dOut[i] * gInv[i];
		}
	}
}
