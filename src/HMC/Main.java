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

		 
		 
//		 HMCDataContainer data = ARFFReader.readFile("toyHMC.arff");
		HMCDataContainer dataTrain = ARFFReader.readFile("datasets/datasets_FUN/church_FUN/church_FUN.train.arff");
		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/church_FUN/church_FUN.test.arff");
		 
		 System.out.println(Utility.isMandatoryLeafNode(dataTrain.hierarchical, dataTrain.dataEntries));
		 System.out.println(Utility.isMandatoryLeafNode(dataTest.hierarchical, dataTest.dataEntries));
		 System.out.println();
		 
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
		
		//nomalize
		for (int i = 0; i < dataTrain.dataEntries.size(); i++) {
			 for(int j=0;j<maxNumericParameter.length;j++){
				 Object parameter = dataTrain.dataEntries.get(i).parameters.get(j);
				 if(parameter instanceof NumericParameter){
					 Double value = ((NumericParameter) parameter).getValue();
					 if(value!=null){
						 ((NumericParameter) parameter).setValue((value-minNumericParameter[j])/(maxNumericParameter[j]-minNumericParameter[j]));
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
						 ((NumericParameter) parameter).setValue((value-minNumericParameter[j])/(maxNumericParameter[j]-minNumericParameter[j]));
					 }
				 }
			 }
		}
		
		KNN.Test(dataTrain, dataTest);
	}

}
