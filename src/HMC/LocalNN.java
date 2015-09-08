package HMC;

import java.io.IOException;
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
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Attribute.NominalAttribute;
import HMC.Container.Attribute.NumericAttribute;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.NominalParameter;
import HMC.Container.Data.NumericParameter;
import HMC.Container.Data.Parameter;
import HMC.Evaluator.ELb;
import HMC.Evaluator.LbMicro;
import HMC.Reader.ARFFReader;

public class LocalNN {

	public static void main(String[] args) throws IOException {
		final String dataset = "eisen_FUN";
		final boolean NEGATIVE_FEATURE = true;
		final double FEATURE_RANGE = 2.0;
		final double[] HIDEEN_NEURAL_FRACTION = new double[] { 0.6, 0.5, 0.4, 0.3, 0.2, 0.1 };
		final int MAX_EPOCH = 1000;

		// Prepare
		HMCDataContainer dataTrain = ARFFReader.readFile("datasets/" + dataset + "/" + dataset + ".train.arff");
		HMCDataContainer dataTest = ARFFReader.readFile("datasets/" + dataset + "/" + dataset + ".test.arff");

		Utility.numericalNormalizer(new HMCDataContainer[] { dataTrain, dataTest }, NEGATIVE_FEATURE, FEATURE_RANGE);

		int nFeature = getFeaturesCount(dataTrain.attributes);
		ArrayList<ArrayList<String>> hierarchicalMapping = getHierarchicalMapping(dataTrain.hierarchical);
		int hierarchySize = hierarchicalMapping.size();
		int[] nClassList = new int[hierarchySize];

		double[][] inputTrain = getInputData(dataTrain, nFeature, NEGATIVE_FEATURE, FEATURE_RANGE);
		double[][] inputTest = getInputData(dataTest, nFeature, NEGATIVE_FEATURE, FEATURE_RANGE);
		ArrayList<double[][]> outputTrain = new ArrayList<double[][]>();
		ArrayList<double[][]> predictedOutputTrain = new ArrayList<double[][]>();
		ArrayList<double[][]> predictedOutputTest = new ArrayList<double[][]>();

		ArrayList<BasicNetwork> networkList = new ArrayList<BasicNetwork>();

		// Train
		for (int i = 0; i < hierarchySize; i++) {
			nClassList[i] = hierarchicalMapping.get(i).size();
			double[][] newOutputTrain = getOutputData(dataTrain, hierarchicalMapping.get(i), nClassList[i]);
			outputTrain.add(newOutputTrain);

			BasicNetwork network = new BasicNetwork();
			int nInput = i == 0 ? nFeature : nClassList[i - 1];
			network.addLayer(new BasicLayer(null, false, nInput));
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (int) (nInput * HIDEEN_NEURAL_FRACTION[i])));
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, nClassList[i]));
			network.getStructure().finalizeStructure();
			network.reset();
			networkList.add(network);

			double[][] trainingData = i == 0 ? inputTrain : predictedOutputTrain.get(i - 1);
			MLDataSet trainingSet = new BasicMLDataSet(trainingData, newOutputTrain);
			// final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
			final Backpropagation train = new Backpropagation(network, trainingSet);
			int epoch = 1;
			do {
				train.iteration();
				// System.out.println("Epoch #" + epoch + " Error:" + train.getError());
				epoch++;
			} while (train.getError() > 0.01 && epoch < MAX_EPOCH);
			System.out.println("Level " + (i + 1) + " Epoch #" + epoch + " Error:" + train.getError());
			train.finishTraining();

			double[][] newPredictedOutputTrain = new double[dataTrain.dataEntries.size()][nClassList[i]];
			for (int j = 0; j < dataTrain.dataEntries.size(); j++) {
				network.compute(trainingData[j], newPredictedOutputTrain[j]);
			}
			predictedOutputTrain.add(newPredictedOutputTrain);
		}

		// Test
		for (int i = 0; i < hierarchySize; i++) {
			BasicNetwork network = networkList.get(i);
			double[][] testingData = i == 0 ? inputTest : predictedOutputTest.get(i - 1);

			double[][] newPredictedOutputTest = new double[dataTest.dataEntries.size()][nClassList[i]];
			for (int j = 0; j < dataTest.dataEntries.size(); j++) {
				network.compute(testingData[j], newPredictedOutputTest[j]);
			}
			predictedOutputTest.add(newPredictedOutputTest);
		}

		// Evaluate result
		double bestThreshold = 0.05, bestF1 = 0.0;
		for (double threshold = 0.00; threshold <= 1.0001; threshold += 0.04) {
			Utility.clearPrediction(dataTest);
			for (int i = 0; i < hierarchySize; i++) {
				assignResult(dataTest, hierarchicalMapping.get(i), predictedOutputTest.get(i), dataTest.hierarchical, threshold);
			}
			HMC.Utility.correctHierarchicalByRemove(dataTest.dataEntries);
			HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
			Double[] eval = ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries, false);
			double f1 = eval[2];
			if (f1 > bestF1) {
				bestF1 = f1;
				bestThreshold = threshold;
			}
			// System.out.println("treshold = " + threshold + ", precision = " + eval[0] + ", recall = " + eval[1] + ", f1 = " + eval[2]);
			eval = LbMicro.Evaluate(dataTest.hierarchical, false);
			System.out.println(threshold + "\t" + eval[0] + "\t" + eval[1] + "\t" + eval[2]);
		}
		// Print best result
		Utility.clearPrediction(dataTest);
		for (int i = 0; i < hierarchySize; i++) {
			assignResult(dataTest, hierarchicalMapping.get(i), predictedOutputTest.get(i), dataTest.hierarchical, bestThreshold);
		}
		HMC.Utility.correctHierarchicalByRemove(dataTest.dataEntries);
		// HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
		System.out.println("================================");
		System.out.println("treshold = " + bestThreshold);
		System.out.println();
		HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
		ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries);

		// Shutdown
		Encog.getInstance().shutdown();
	}

	private static ArrayList<ArrayList<String>> getHierarchicalMapping(Hierarchical hierarchical) {
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		while (true) {
			ArrayList<String> newLevelList = new ArrayList<String>();
			if (res.size() == 0) {
				for (HierarchicalNode node : hierarchical.root.children) {
					if (!newLevelList.contains(node.getFullId())) {
						newLevelList.add(node.getFullId());
					}
				}
			} else {
				ArrayList<String> lastLevelList = res.get(res.size() - 1);
				for (int i = 0; i < lastLevelList.size(); i++) {
					HierarchicalNode parentNode = hierarchical.hierarchicalMapping.get(lastLevelList.get(i));
					for (HierarchicalNode node : parentNode.children) {
						if (!newLevelList.contains(node.getFullId())) {
							newLevelList.add(node.getFullId());
						}
					}
				}
			}
			if (newLevelList.size() == 0) {
				break;
			}
			res.add(newLevelList);
		}
		return res;
	}

	private static int getFeaturesCount(ArrayList<Attribute> attrs) {
		int res = 0;
		for (Attribute attributes : attrs) {
			if (attributes instanceof NominalAttribute) {
				res += ((NominalAttribute) attributes).getPossibleValue().size();
			} else if (attributes instanceof NumericAttribute) {
				res += 1;
			}
		}
		return res;
	}

	private static double[][] getInputData(HMCDataContainer dataContainer, int nFeature, boolean useNegative, double range) {
		double[][] res = new double[dataContainer.dataEntries.size()][nFeature];
		for (int i = 0; i < dataContainer.dataEntries.size(); i++) {
			int j = 0;
			for (Parameter param : dataContainer.dataEntries.get(i).parameters) {
				if (param instanceof NumericParameter) {
					if (param.hasValue()) {
						res[i][j++] = ((NumericParameter) param).getValue();
					} else {
						res[i][j++] = useNegative ? 0 : range / 2.0;
					}
				} else if (param instanceof NominalParameter) {
					ArrayList<String> possibleValue = ((NominalAttribute) (((NominalParameter) param).getAttribute())).getPossibleValue();
					for (String value : possibleValue) {
						if (param.getValue() == null) {
							res[i][j++] = useNegative ? 0 : range / 2.0;
							continue;
						}
						if (value.equalsIgnoreCase((String) param.getValue())) {
							res[i][j++] = useNegative ? range / 2.0 : range;
						} else {
							res[i][j++] = useNegative ? -range / 2.0 : 0;
						}
					}
				}
			}
			if (j != nFeature) {
				System.out.println("j=" + j + "countInput=" + nFeature);
			}
		}
		return res;
	}

	private static double[][] getOutputData(HMCDataContainer dataContainer, ArrayList<String> classMapping, int nClass) {
		double[][] res = new double[dataContainer.dataEntries.size()][nClass];
		for (int i = 0; i < dataContainer.dataEntries.size(); i++) {
			int j = 0;
			for (String label : classMapping) {
				if (dataContainer.dataEntries.get(i).hasLabel(label)) {
					res[i][j++] = 1.0;
				} else {
					res[i][j++] = 0.0;
				}
			}
			if (j != nClass) {
				System.out.println("j=" + j + "countInput=" + nClass);
			}
		}
		return res;
	}

	private static void assignResult(HMCDataContainer dataContainer, ArrayList<String> classMapping, double[][] results, Hierarchical hierarchical, double threshold) {
		for (int i = 0; i < dataContainer.dataEntries.size(); i++) {
			for (int j = 0; j < results[i].length; j++) {
				if (results[i][j] > threshold) {
					DataEntry dataEntry = dataContainer.dataEntries.get(i);
					HierarchicalNode node = hierarchical.hierarchicalMapping.get(classMapping.get(j));
					dataEntry.addPredictedLabel(node);
					node.addPredictedMember(dataEntry);
				}
			}
		}
	}
}
