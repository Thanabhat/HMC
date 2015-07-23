package thanabhat.nn;

import java.util.Random;

public abstract class AbstractOutputLayer extends AbstractLayer {

	public AbstractOutputLayer(int nIn, int nOut, double[][] w, double[] b,
			Random rng) {
		super(nIn, nOut, w, b, rng);
	}

	abstract public void train(double[][] y, double[][] yActual, double lr, double lrReg, double[] dPrev);
}
