package HMC;

import java.util.ArrayList;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import HMC.Container.HMCDataContainer;
import HMC.Container.Attribute.Attribute;
import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.NominalAttribute;
import HMC.Container.Attribute.NumericAttribute;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.NominalParameter;
import HMC.Container.Data.NumericParameter;
import HMC.Container.Data.Parameter;
import HMC.Evaluator.ELb;
import thanabhat.nn.*;

public class NeuralNetworkConnector {

	private BasicNetwork network;
	private HMCDataContainer dataTrain, dataTest;
	private ArrayList<String> outputLabelOrder;
	private int countInput, countOutput;
	private double[][] inputTrain, inputTest, outputTrain, outputTest;
	private double THRESHOLD = 0.12;
	private long timeStart;

	public NeuralNetworkConnector(HMCDataContainer dataTrain, HMCDataContainer dataTest) {
		// TODO Auto-generated constructor stub

		timeStart = System.currentTimeMillis();

		this.dataTrain = dataTrain;
		this.dataTest = dataTest;

		PrepareInputVariable(dataTrain.attributes);
		PrepareOutputVariable(dataTrain.hierarchical);

		inputTrain = getInputData(dataTrain);
		outputTrain = getOutputData(dataTrain);
		inputTest = getInputData(dataTest);
		outputTest = new double[dataTest.dataEntries.size()][countOutput];

		encogNeuralNetwork();
//		thanabhatMLP();

		for (double t = 0.05; t <= 0.305; t += 0.005) {
			this.THRESHOLD = t;

			Utility.clearPrediction(dataTest);
			for (int i = 0; i < dataTest.dataEntries.size(); i++) {
				assignResult(dataTest.dataEntries.get(i), outputTest[i], dataTest.hierarchical);
			}

			System.out.println(this.THRESHOLD);
			HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
			ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries, false);

			System.out.println("Correct Predicted Label:");
			HMC.Utility.correctHierarchical(dataTest.dataEntries);
			HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
			ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries, true);
		}

		long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
		System.out.println("Time: " + elapsedTimeMillis + " ms");
		System.out.println();
	}

	private void thanabhatMLP() {
		int[] hiddenLayerSizes = new int[] { (int) (1.0 * (countInput + countOutput) / 2.0) };
		MLP mlp = new MLP(countInput, countOutput, hiddenLayerSizes, null);
		double error;
		final int maxEpochs = 30;
		int epochs = 0;
		do {
			error = mlp.train(inputTrain, outputTrain, 1, 0.001);
			System.out.println("epoch " + epochs + ", error: " + error);
			epochs++;
		} while (error > 0.01 && epochs < maxEpochs);

		mlp.predict(inputTest, outputTest);
	}

	private void encogNeuralNetwork() {

		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, countInput));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (countInput+countOutput)/2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, countOutput));
		network.getStructure().finalizeStructure();
		network.reset();

		// create training data
		MLDataSet trainingSet = new BasicMLDataSet(inputTrain, outputTrain);

		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
//		final Backpropagation train = new Backpropagation(network, trainingSet);
//		System.out.println(train.getLearningRate());

		int epoch = 1;

		do {
			train.iteration();
			 System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.015);
		train.finishTraining();

		// test the neural network
		// System.out.println("Neural Network Results:");
		// for(MLDataPair pair: trainingSet ) {
		// final MLData output = network.compute(pair.getInput());
		// for(int i=0;i<countOutput;i++){
		// // if(pair.getIdeal().getData(i)>1E-8){
		// System.out.print("{" + output.getData(i) + "," +
		// pair.getIdeal().getData(i) + "}");
		// // }
		// }
		// System.out.println("");
		// }

		for (int i = 0; i < dataTest.dataEntries.size(); i++) {
			network.compute(inputTest[i], outputTest[i]);
		}

		Encog.getInstance().shutdown();
	}

	private void PrepareInputVariable(ArrayList<Attribute> attrs) {
		countInput = 0;
		for (Attribute attributes : attrs) {
			if (attributes instanceof NominalAttribute) {
				countInput += ((NominalAttribute) attributes).getPossibleValue().size();
			} else if (attributes instanceof NumericAttribute) {
				countInput += 1;
			}
		}
	}

	private void PrepareOutputVariable(Hierarchical hierarchical) {
		outputLabelOrder = new ArrayList<String>();
		for (String key : hierarchical.hierarchicalMapping.keySet()) {
			outputLabelOrder.add(key);
		}
		countOutput = outputLabelOrder.size();
	}

	private double[][] getInputData(HMCDataContainer dataContainer) {
		double[][] res = new double[dataContainer.dataEntries.size()][countInput];
		for (int i = 0; i < dataContainer.dataEntries.size(); i++) {
			int j = 0;
			for (Parameter param : dataContainer.dataEntries.get(i).parameters) {
				if (param instanceof NumericParameter) {
					if (param.hasValue()) {
						res[i][j++] = ((NumericParameter) param).getValue();
					} else {
						res[i][j++] = 0.5;
					}
				} else if (param instanceof NominalParameter) {
					ArrayList<String> possibleValue = ((NominalAttribute) (((NominalParameter) param).getAttribute())).getPossibleValue();
					for (String value : possibleValue) {
						if (param.getValue() == null) {
							res[i][j++] = 0.0;
							continue;
						}
						if (value.equalsIgnoreCase((String) param.getValue())) {
							res[i][j++] = 0.5;
						} else {
							res[i][j++] = -0.5;
						}
					}
				}
			}
			if (j != countInput) {
				System.out.println("j=" + j + "countInput=" + countInput);
			}
		}
		return res;
	}

	private double[][] getOutputData(HMCDataContainer dataContainer) {
		double[][] res = new double[dataContainer.dataEntries.size()][countOutput];
		for (int i = 0; i < dataContainer.dataEntries.size(); i++) {
			int j = 0;
			for (String label : outputLabelOrder) {
				if (dataContainer.dataEntries.get(i).hasLabel(label)) {
					res[i][j++] = 1.0;
				} else {
					res[i][j++] = 0.0;
				}
			}
			if (j != countOutput) {
				System.out.println("j=" + j + "countInput=" + countInput);
			}
		}
		return res;
	}

	private void assignResult(DataEntry dataEntry, double[] results, Hierarchical hierarchical) {
		for (int i = 0; i < results.length; i++) {
			if (results[i] > THRESHOLD) {
				dataEntry.addPredictedLabel(hierarchical.hierarchicalMapping.get(outputLabelOrder.get(i)));
				hierarchical.hierarchicalMapping.get(outputLabelOrder.get(i)).addPredictedMember(dataEntry);
			}
		}
		// correctHierarchical(dataEntry);
	}
}
