package thanabhat.nn;

import java.util.ArrayList;
import java.util.Random;

public class MLP {
	int nIn, nOut, nLayers;
	int[] layerSizes;
	Random rng;
	BasicLayer[] layers;

	public MLP(int nIn, int nOut, int[] hiddenLayerSizes, Random rng) {
		this.nIn = nIn;
		this.nOut = nOut;
		int nHiddenLayers = hiddenLayerSizes.length;
		this.layerSizes = new int[nHiddenLayers + 1];
		for (int i = 0; i < nHiddenLayers; i++) {
			this.layerSizes[i] = hiddenLayerSizes[i];
		}
		layerSizes[nHiddenLayers] = nOut;
		this.nLayers = nHiddenLayers + 1;
		this.rng = rng;

		initLayers();
	}

	protected void initLayers() {
		layers = new BasicLayer[nLayers];
		for (int i = 0; i < nLayers; i++) {
			if (i < nLayers - 1) {
				layers[i] = new BasicLayer(i == 0 ? nIn : layerSizes[i - 1], layerSizes[i], null, null, rng);
			} else {
				layers[i] = new OutputLayer(i == 0 ? nIn : layerSizes[i - 1], layerSizes[i], null, null, rng);
			}
		}
	}

	public double train(double[][] x, int[][] y, double lr, double lrReg) {
		int m = x.length;
		double[] err = new double[m];
		for (int i = 0; i < m; i++) {
			// Forward propagation
			ArrayList<double[]> zList = new ArrayList<double[]>();
			ArrayList<double[]> aList = new ArrayList<double[]>();
			for (int j = 0; j < nLayers; j++) {
				double[] z = new double[layerSizes[j]];
				double[] a = new double[layerSizes[j]];
				layers[j].fwdProp(j == 0 ? x[i] : aList.get(j - 1), z, a);
				zList.add(z);
				aList.add(a);
			}

			// Calculate difference between actual result and predicted result
			double[] diff = new double[nOut];
			double[] yPred = aList.get(nLayers - 1);
			err[i] = 0;
			for (int j = 0; j < nOut; j++) {
				diff[j] = (double) yPred[j] - y[i][j];
				err[i] += Math.pow(diff[j], 2);
			}

			// Back propagation
			ArrayList<double[]> dList = new ArrayList<double[]>();
			for (int j = nLayers - 1; j >= 0; j--) {
				if (j == nLayers - 1) {
					double[] d = new double[layerSizes[j]];
					((OutputLayer) layers[j]).calcD(diff, zList.get(j), d);
					dList.add(0, d);
				}
				layers[j].updateWeight(dList.get(0), j == 0 ? x[i] : aList.get(j - 1), m, lr, lrReg);
				if (j > 0) {
					double[] d = new double[layerSizes[j - 1]];
					layers[j].bckProp(dList.get(0), zList.get(j - 1), d);
					dList.add(0, d);
				}
			}
		}
		return Util.average(err) / 2.0;
	}
	
	public void printWeight() {
		for (int i = 0; i < nLayers; i++) {
			System.out.println("layer: " + i);
			layers[i].printWeight();
		}
	}

	public static void main(String[] args) {

		double[][] trainX = {
			{1, 1, 1, 0, 0, 0},
			{1, 0, 1, 0, 0, 0},
			{1, 1, 1, 0, 0, 0},
			{0, 0, 1, 1, 1, 0},
			{0, 0, 1, 1, 0, 0},
			{0, 0, 1, 1, 1, 0}
		};
		int[][] trainY = {
			{1, 0},
			{1, 0},
			{1, 0},
			{0, 1},
			{0, 1},
			{0, 1},
		};
		int[] hiddenLayerSize = {4};

		MLP mlp = new MLP(trainX[0].length, trainY[0].length, hiddenLayerSize, null);
		for (int i = 0; i < 100; i++) {
			System.out.println("============================ iterate " + (i + 1));
			System.out.println(mlp.train(trainX, trainY, 0.1, 0.001));
			mlp.printWeight();
		}
	}

}
