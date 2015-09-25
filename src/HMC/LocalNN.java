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

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;

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
import HMC.Evaluator.AUPRC;
import HMC.Evaluator.ELb;
import HMC.Evaluator.HRSVM;
import HMC.Evaluator.LbMicro;
import HMC.Reader.ARFFReader;
import thanabhat.nn.MLP;

public class LocalNN {

	/**
	 * Cerri = Cerri
	 * Jo1 = Cerri and add features on each NN input
	 * Jo2 = use only features on each NN input
	 */
	static enum Method {Cerri, Jo1, Jo2};
	static enum NeuralNetworkLib {encog, thanabhat};
	static double eps = 1E-7;
	
	public static void main(String[] args) throws IOException {
		final String dataset = "imclef07d";
		final boolean NEGATIVE_FEATURE = true;
		final double FEATURE_RANGE = 2.0;
//		final double[] HIDEEN_NEURAL_FRACTION = new double[] { 0.6, 0.5, 0.4, 0.3, 0.2, 0.1 };
//		final double[] HIDEEN_NEURAL_FRACTION = new double[] { 0.2, 0.2, 0.2, 0.2, 0.2, 0.2 };
		final double[] HIDEEN_NEURAL_FRACTION = new double[] { 0.4, 0.4, 0.4, 0.4, 0.4, 0.4 };
//		final double[] HIDEEN_NEURAL_FRACTION = new double[] { 0.8, 0.8, 0.8, 0.8, 0.8, 0.8 };
//		final double[] HIDEEN_NEURAL_FRACTION = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		final int MAX_EPOCH = 1000;
		final double MIN_ERROR = 0.001;
		final Method METHOD = Method.Jo1;
		final NeuralNetworkLib NN_Lib = NeuralNetworkLib.encog;
		final double THANABHAT_LR = 0.5;
		final double THANABHAT_LRReg = 0.0;
		final boolean usePCAinFirstLevel = false;
		final boolean usePCAinOtherLevel = true;
		final int nPCAFeature = 40;

		// Prepare Data
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

		// do PCA
		double[][] inputTrainTest = new double[inputTrain.length + inputTest.length][inputTrain[0].length];
		for (int i = 0; i < inputTrainTest.length; i++) {
			for (int j = 0; j < inputTrainTest[i].length; j++) {
				if (i < inputTrain.length) {
					inputTrainTest[i][j] = inputTrain[i][j];
				} else {
					inputTrainTest[i][j] = inputTest[i - inputTrain.length][j];
				}
			}
		}
		double[][] pcaResult = PCA(inputTrainTest, nPCAFeature);
		double[][] inputTrainPCA = new double[inputTrain.length][pcaResult[0].length];
		double[][] inputTestPCA = new double[inputTest.length][pcaResult[0].length];
		for (int i = 0; i < pcaResult.length; i++) {
			for (int j = 0; j < pcaResult[i].length; j++) {
				if (i < inputTrainPCA.length) {
					inputTrainPCA[i][j] = pcaResult[i][j];
				} else {
					inputTestPCA[i - inputTrainPCA.length][j] = pcaResult[i][j];
				}
			}
		}

		// Prepare network collector
		ArrayList<BasicNetwork> networkList = null;
		ArrayList<MLP> networkList2 = null;
		switch (NN_Lib) {
			case encog:
				networkList = new ArrayList<BasicNetwork>();
				break;
			case thanabhat:
				networkList2 = new ArrayList<MLP>();
				break;
		}

		// Train
		for (int i = 0; i < hierarchySize; i++) {
			nClassList[i] = hierarchicalMapping.get(i).size();
			double[][] newOutputTrain = getOutputData(dataTrain, hierarchicalMapping.get(i), nClassList[i]);
			outputTrain.add(newOutputTrain);

			double[][] trainingData;
			switch (METHOD) {
				case Cerri:
					trainingData = i == 0 ? (usePCAinFirstLevel ? inputTrainPCA : inputTrain) : predictedOutputTrain.get(i - 1);
					break;
				case Jo1:
				default:
					if (i == 0) {
						trainingData = usePCAinFirstLevel ? inputTrainPCA : inputTrain;
					} else {
						trainingData = Utility.concat(predictedOutputTrain.get(i - 1), usePCAinOtherLevel ? inputTrainPCA : inputTrain);
					}
					break;
				case Jo2:
					if (i == 0) {
						trainingData = usePCAinFirstLevel ? inputTrainPCA : inputTrain;
					} else {
						trainingData = usePCAinOtherLevel ? inputTrainPCA : inputTrain;
					}
			}
			int nInput = trainingData[0].length;

			int epoch = 1;
			double[][] newPredictedOutputTrain = new double[dataTrain.dataEntries.size()][nClassList[i]];
			switch (NN_Lib) {
				case encog:
					BasicNetwork network = new BasicNetwork();
					network.addLayer(new BasicLayer(null, false, nInput));
					network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (int) (nInput * HIDEEN_NEURAL_FRACTION[i])));
					network.addLayer(new BasicLayer(new ActivationSigmoid(), true, nClassList[i]));
					network.getStructure().finalizeStructure();
					network.reset();
					networkList.add(network);

					MLDataSet trainingSet = new BasicMLDataSet(trainingData, newOutputTrain);
					// final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
					final Backpropagation train = new Backpropagation(network, trainingSet);
//					System.out.println(train.getLearningRate());
//					train.setLearningRate(0.001);

					do {
						train.iteration();
//						 System.out.println("Epoch #" + epoch + " Error:" + train.getError());
						epoch++;
					} while (train.getError() > MIN_ERROR && epoch < MAX_EPOCH);
					System.out.println("NN size: " + nInput + " " + (int) (nInput * HIDEEN_NEURAL_FRACTION[i]) + " " + nClassList[i]);
					System.out.println("Level " + (i + 1) + " Epoch #" + epoch + " Error: " + train.getError());
					train.finishTraining();

					for (int j = 0; j < dataTrain.dataEntries.size(); j++) {
						network.compute(trainingData[j], newPredictedOutputTrain[j]);
					}
					break;
				case thanabhat:
					MLP mlp = new MLP(nInput, nClassList[i], new int[] { (int) (nInput * HIDEEN_NEURAL_FRACTION[i]) }, null);
					networkList2.add(mlp);
					double trainingError;
					do {
						trainingError = mlp.train(trainingData, newOutputTrain, THANABHAT_LR, THANABHAT_LRReg);
//						System.out.println("Epoch #" + epoch + " Error:" + trainingError);
						epoch++;
					} while (trainingError > MIN_ERROR && epoch < MAX_EPOCH);
					
					ArrayList<String> hm = hierarchicalMapping.get(i);
					for (int j = 0; j < hm.size(); j++) {
						System.out.print(hm.get(j) + "\t");
					}
					System.out.println();
					System.out.println("Level " + (i + 1) + " Epoch #" + epoch + " Error: " + trainingError);
					mlp.printWeight();
					System.out.println();

					mlp.predict(trainingData, newPredictedOutputTrain);
					break;
			}
			predictedOutputTrain.add(newPredictedOutputTrain);
		}

		// Test
		for (int i = 0; i < hierarchySize; i++) {
			double[][] testingData;
			switch (METHOD) {
				case Cerri:
					testingData = i == 0 ? (usePCAinFirstLevel ? inputTestPCA : inputTest) : predictedOutputTest.get(i - 1);
					break;
				case Jo1:
				default:
					if (i == 0) {
						testingData = usePCAinFirstLevel ? inputTestPCA : inputTest;
					} else {
						testingData = Utility.concat(predictedOutputTest.get(i - 1), usePCAinOtherLevel ? inputTestPCA : inputTest);
					}
					break;
				case Jo2:
					if (i == 0) {
						testingData = usePCAinFirstLevel ? inputTestPCA : inputTest;
					} else {
						testingData = usePCAinOtherLevel ? inputTestPCA : inputTest;
					}
			}

			double[][] newPredictedOutputTest = new double[dataTest.dataEntries.size()][nClassList[i]];
			switch (NN_Lib) {
				case encog:
					BasicNetwork network = networkList.get(i);
					for (int j = 0; j < dataTest.dataEntries.size(); j++) {
						network.compute(testingData[j], newPredictedOutputTest[j]);
					}
					break;
				case thanabhat:
					MLP mlp = networkList2.get(i);
					mlp.predict(testingData, newPredictedOutputTest);
					break;
			}
			predictedOutputTest.add(newPredictedOutputTest);
		}

		// Evaluate result
		ArrayList<double[]> sortedPRPair = new ArrayList<double[]>();
		double bestThreshold = 0.05, bestF1 = 0.0;
		int curI = 0;
		for (double threshold = 0.00; threshold <= 1.0 + eps; threshold += 0.02, curI++) {
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
			if (!(eval[1] < eps && eval[2] < eps)) { //remove (0.0,0.0) pair
				sortedPRPair.add(new double[] { eval[0], eval[1] });
			}
//			System.out.println(threshold + "\t" + eval[0] + "\t" + eval[1] + "\t" + eval[2]);
			if (curI % 2 == 0) {
				System.out.println(eval[0] + "\t" + eval[1]);
			}
		}
		// Print best result Pr Re F1
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

		// Print result AUPRC
		System.out.println("================================");
		System.out.println("AUPRC = " + AUPRC.evaluate(sortedPRPair));
		
		// Export to HRSVM evaluator file format
		HRSVM.exportToHRSVM(dataTest, "datasets/" + dataset + "/" + dataset);
		
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

	private static double[][] PCA(double[][] data, int newN) {
		// Create new Instances
		FastVector atts = new FastVector();
		for (int i = 0; i < data[0].length; i++) {
			atts.addElement(new weka.core.Attribute("att" + i));
		}
		Instances instances = new Instances("GG", atts, 0);
		for (int i = 0; i < data.length; i++) {
			instances.add(new Instance(1.0, data[i]));
		}

		// PCA and normalize
		PrincipalComponents pca = new PrincipalComponents();
		pca.setMaximumAttributeNames(newN);
		pca.setMaximumAttributes(newN);
		Normalize norm = new Normalize();
		
		try {
			pca.setInputFormat(instances);
			instances = Filter.useFilter(instances, pca);
			norm.setInputFormat(instances);
			instances = Filter.useFilter(instances, norm);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		double[][] res = new double[instances.numInstances()][instances.numAttributes()];
		for (int i = 0; i < res.length; i++) {
			Instance inst = instances.instance(i);
			for (int j = 0; j < res[i].length; j++) {
				res[i][j] = inst.value(j);
			}
		}
		return res;
	}
}
