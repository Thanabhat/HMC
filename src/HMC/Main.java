package HMC;

import java.util.ArrayList;

import HMC.Container.HMCDataContainer;
import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Attribute.NominalAttribute;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.NominalParameter;
import HMC.Container.Data.NumericParameter;
import HMC.Evaluator.ELb;
import HMC.Evaluator.Eb;
import HMC.Evaluator.LbMacro;
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
		 Double[] maxNumericParameter = new Double[dataTrain.attributes.size()];
		 Double[] minNumericParameter = new Double[dataTrain.attributes.size()];
		 java.util.Arrays.fill(maxNumericParameter, -1000000.0);
		 java.util.Arrays.fill(minNumericParameter, 1000000.0);
		 
		for (int i = 0; i < dataTrain.dataEntries.size(); i++) {
			 for(int j=0;j<maxNumericParameter.length;j++){
				 Object parameter = dataTrain.dataEntries.get(i).parameters.get(j);
				 if(parameter instanceof NumericParameter){
					 Double value = ((NumericParameter) parameter).getValue();
					 if(value!=null){
						 maxNumericParameter[j] = maxNumericParameter[j]>value?maxNumericParameter[j]:value;
						 minNumericParameter[j] = minNumericParameter[j]<value?minNumericParameter[j]:value;
					 }
				 }
			 }
		}
		for (int i = 0; i < dataTest.dataEntries.size(); i++) {
			 for(int j=0;j<maxNumericParameter.length;j++){
				 Object parameter = dataTest.dataEntries.get(i).parameters.get(j);
				 if(parameter instanceof NumericParameter){
					 Double value = ((NumericParameter) parameter).getValue();
					 if(value!=null){
						 maxNumericParameter[j] = maxNumericParameter[j]>value?maxNumericParameter[j]:value;
						 minNumericParameter[j] = minNumericParameter[j]<value?minNumericParameter[j]:value;
					 }
				 }
			 }
		}
		
		//normalize
		for (int i = 0; i < dataTrain.dataEntries.size(); i++) {
			 for(int j=0;j<maxNumericParameter.length;j++){
				 Object parameter = dataTrain.dataEntries.get(i).parameters.get(j);
				 if(parameter instanceof NumericParameter){
					 Double value = ((NumericParameter) parameter).getValue();
					 if(value!=null){
						 if(maxNumericParameter[j]!=minNumericParameter[j]){
							 ((NumericParameter) parameter).setValue((value-minNumericParameter[j])/(maxNumericParameter[j]-minNumericParameter[j]));
						 }else{
							 ((NumericParameter) parameter).setValue(0.5);
						 }
					 }
				 }
			 }
		}
		for (int i = 0; i < dataTest.dataEntries.size(); i++) {
			 for(int j=0;j<maxNumericParameter.length;j++){
				 Object parameter = dataTest.dataEntries.get(i).parameters.get(j);
				 if(parameter instanceof NumericParameter){
					 Double value = ((NumericParameter) parameter).getValue();
					 if(value!=null){
						 if(maxNumericParameter[j]!=minNumericParameter[j]){
							 ((NumericParameter) parameter).setValue((value-minNumericParameter[j])/(maxNumericParameter[j]-minNumericParameter[j]));
						 }else{
							 ((NumericParameter) parameter).setValue(0.5);
						 }
					 }
				 }
			 }
		}

		 
//		dataTrain.hierarchical.printHierarchical();
		
		System.out.println("#### K-NN ####\n");
		KNN.Test(dataTrain, dataTest);
		
		System.out.println("#### Neural Network ####\n");
		dataTest.hierarchical.clearAllPredictedMember();
		new NeuralNetwork(dataTrain, dataTest);
		
//		HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
		

		System.out.println("#### Clus ####\n");
//		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/church_FUN/church_FUN.test.arff");
		HMCDataContainer clusPrediction = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.pred.arff");

		for(double d =0.1;d<=0.201;d+=0.01){
			System.out.println(d);
			dataTest.hierarchical.clearAllPredictedMember();
			Utility.assignClusPredictionToContainer(dataTest, clusPrediction, d);
//			HMC.Evaluator.Utility.printResult(dataTest.dataEntries);
			HMC.Evaluator.Utility.PrepareParameter(dataTest.hierarchical);
			ELb.Evaluate(dataTest.hierarchical, dataTest.dataEntries);
		}
		
		System.out.println("done");
	}

}
