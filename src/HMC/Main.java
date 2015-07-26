package HMC;

import HMC.Container.HMCDataContainer;
import HMC.Evaluator.ELb;
import HMC.Reader.ARFFReader;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		final String dataset = "church_FUN";

//		 HMCDataContainer data = ARFFReader.readFile("toyHMC.arff");
		HMCDataContainer dataTrain = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".train.arff");
		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.arff");
		 
//		 System.out.println(Utility.isMandatoryLeafNode(dataTrain.hierarchical, dataTrain.dataEntries));
//		 System.out.println(Utility.isMandatoryLeafNode(dataTest.hierarchical, dataTest.dataEntries));
//		 System.out.println();
		 
		 //normalize numeric data
		Utility.numericalNormalizer(new HMCDataContainer[]{dataTrain,dataTest});

		 
//		dataTrain.hierarchical.printHierarchical();
		
//		System.out.println("#### K-NN ####\n");
//		KNN.Test(dataTrain, dataTest);
		
		System.out.println("#### Neural Network ####\n");
		dataTest.hierarchical.clearAllPredictedMember();
		new NeuralNetworkConnector(dataTrain, dataTest);
		
//		HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
		

//		System.out.println("#### Clus ####\n");
////		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/church_FUN/church_FUN.test.arff");
//		HMCDataContainer clusPrediction = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.pred.arff");
//
//		for(double d =0.1;d<=0.201;d+=0.01){
//			System.out.println(d);
//			dataTest.hierarchical.clearAllPredictedMember();
//			Utility.assignClusPredictionToContainer(dataTest, clusPrediction, d);
////			HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
//			HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
//			ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries, false);
//		}
		
		System.out.println("done");
	}

}
