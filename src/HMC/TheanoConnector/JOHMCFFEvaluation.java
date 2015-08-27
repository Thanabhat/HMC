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
		final String dataset = "imclef07d";
		final boolean useMLNP = false;

		HMCDataContainer dataTest = ARFFReader.readFile("datasets/"+dataset+"/"+dataset+".test.arff");
//		System.out.println(Utility.isMandatoryLeafNode(dataTest.hierarchical, dataTest.dataEntries));		

		if (useMLNP) {
			Utility.createMandatoryLeafNode(dataTest);
		}
		
		HMCDataContainer joPrediction = ARFFReader.readFile("datasets/"+dataset+"/"+dataset+".test.pred.johmcff");
		
		double bestT = 0.1, bestF1 = 0.0;
		for(double t = 0.1;t<0.4;t+=0.01){
//			System.out.println("================================");
//			System.out.println("treshold = "+t);
//			System.out.println();
			Utility.clearPrediction(dataTest);
			Utility.assignClusPredictionToContainer(dataTest, joPrediction, t);
			if (useMLNP) {
				HMC.Utility.correctHierarchicalByAdd(dataTest.dataEntries);
			} else {
				HMC.Utility.correctHierarchical(dataTest.dataEntries);
			}
//			HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
			HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
			Double[] eval = ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries, false);
			double f1 = eval[2];
			if(f1>bestF1){
				bestF1=f1;
				bestT=t;
			}

			System.out.println("treshold = "+t+", precision = "+eval[0]+", recall = "+eval[1]+", f1 = "+eval[2]);
		}

		dataTest.hierarchical.clearAllPredictedMember();
		Utility.assignClusPredictionToContainer(dataTest, joPrediction, bestT);
		if (useMLNP) {
			HMC.Utility.correctHierarchicalByAdd(dataTest.dataEntries);
		} else {
			HMC.Utility.correctHierarchical(dataTest.dataEntries);
		}
		HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
		System.out.println("================================");
		System.out.println("treshold = "+bestT);
		System.out.println();
		HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
		ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries);
	}

}
