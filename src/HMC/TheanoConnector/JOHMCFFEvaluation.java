package HMC.TheanoConnector;

import java.io.IOException;

import HMC.Utility;
import HMC.Container.HMCDataContainer;
import HMC.Evaluator.ELb;
import HMC.Reader.ARFFReader;

public class JOHMCFFEvaluation {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final String dataset = "church_FUN";

		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.arff");
//		System.out.println(Utility.isMandatoryLeafNode(dataTest.hierarchical, dataTest.dataEntries));		 
		
		HMCDataContainer joPrediction = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.pred.johmcff");
//		HMCDataContainer joPrediction = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.pred.mlp.johmcff");
//		HMCDataContainer joPrediction = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.pred.mlpzero.johmcff");
		dataTest.hierarchical.clearAllPredictedMember();
		Utility.assignClusPredictionToContainer(dataTest, joPrediction, 0.5);
//		HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
		HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
		ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries);
	}

}
