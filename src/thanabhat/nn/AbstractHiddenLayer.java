package thanabhat.nn;

import java.util.Random;

public abstract class AbstractHiddenLayer extends AbstractLayer {

	public AbstractHiddenLayer(int nIn, int nOut, double[][] w, double[] b,
			Random rng) {
		super(nIn, nOut, w, b, rng);
	}

	abstract public void train(double[] d, double[] dPrev, double lr, double lrReg);
}
