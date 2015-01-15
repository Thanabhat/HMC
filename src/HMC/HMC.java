package HMC;

import java.util.ArrayList;

import HMC.Reader.*;
import HMC.Container.*;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Attribute.NominalAttribute;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.NominalParameter;
import HMC.Container.Data.NumericParameter;

public class HMC {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		 
		 
//		 HMCDataContainer data = ARFFReader.readFile("toyHMC.arff");
		HMCDataContainer dataTrain = ARFFReader.readFile("datasets/datasets_FUN/church_FUN/church_FUN.train.arff");
		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/church_FUN/church_FUN.test.arff");
		 
		 System.out.println(Utility.isMandatoryLeafNode(dataTrain.hierarchical, dataTrain.dataEntries));
		 System.out.println(Utility.isMandatoryLeafNode(dataTest.hierarchical, dataTest.dataEntries));

		 
		 //K-NN
		 
		 //prepare numeric range
		 Double[] maxNumericParameter = new Double[dataTest.attributes.size()];
		 Double[] minNumericParameter = new Double[dataTest.attributes.size()];
		 
		 java.util.Arrays.fill(maxNumericParameter, -1000000.0);
		 java.util.Arrays.fill(minNumericParameter, 1000000.0);
		 
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
		 
		//predict
//		int countRightPrediction = 0;
//		int countAll = 0;
		for (int i = 0; i < dataTest.dataEntries.size(); i++) {
//			System.out.print(".");
			Double minDistance = 1000000.0;
			int minDistanceIndex = -1;
			DataEntry dataEntryTest = dataTest.dataEntries.get(i);
			for (int j = 0; j < dataTrain.dataEntries.size(); j++) {
				DataEntry dataEntryTrain = dataTrain.dataEntries.get(j);
				Double distance = 0.0;
				for (int k = 0; k < dataEntryTest.parameters.size(); k++) {
					Object testValue = dataEntryTest.parameters.get(k).getValue();
					Object trainValue = dataEntryTrain.parameters.get(k).getValue();
					if(dataEntryTest.parameters.get(k) instanceof NumericParameter){
						if(testValue!=null&&trainValue!=null){
							Double diff = Math.abs((Double)testValue-(Double)trainValue);
							diff /= (maxNumericParameter[k]-minNumericParameter[k]);
							distance += Math.pow(diff, 2.0);
						}else{
							distance += 1.0;
						}
					}else if(dataEntryTest.parameters.get(k) instanceof NominalParameter){
						distance += getNominalDistance(
								(NominalAttribute)((NominalParameter)dataEntryTest.parameters.get(k)).getAttribute(), 
								(NominalParameter)dataEntryTest.parameters.get(k), 
								(NominalParameter)dataEntryTrain.parameters.get(k));
					}
				}
				if(distance<minDistance){
					minDistance=distance;
					minDistanceIndex=j;
				}
			}
			
			ArrayList<HierarchicalNode> predictLabel = dataTrain.dataEntries.get(minDistanceIndex).getLabel();
			//add predicted label
			for(HierarchicalNode node:predictLabel){
				dataEntryTest.addPredictedLabel(node);
				node.addPredictedMember(dataEntryTest);
			}
			
//			ArrayList<HierarchicalNode> realLabel = dataTest.dataEntries.get(i).getLabel();
			
//			int count=0;
//			for(int ii=0;ii<realLabel.size();ii++){
//				for(int jj=0;jj<predictLabel.size();jj++){
//					if(realLabel.get(ii).fullId.equals(predictLabel.get(jj).fullId)){
//						count++;
//					}
//				}
//			}
//			System.out.print(i+":"+count+"/"+realLabel.size());
//			System.out.print(" : ");
//			for(int ii=0;ii<realLabel.size();ii++){
//				if(ii>0){
//					System.out.print(", ");
//				}
//				System.out.print(realLabel.get(ii).getFullId());
//			}
//			System.out.print(" : ");
//			for(int ii=0;ii<predictLabel.size();ii++){
//				if(ii>0){
//					System.out.print(", ");
//				}
//				System.out.print(predictLabel.get(ii).getFullId());
//			}
//			System.out.println("");
//			if(count==realLabel.size()){
//				countRightPrediction++;
//			}
//			countAll += count;
			
//			System.out.print(i+"="+minDistanceIndex+",");
		}
		 
//		 dataTrain.hierarchical.printHierarchical();
		 
//		 System.out.println(countAll);
//		 System.out.println(countRightPrediction);
		System.out.println("done");
	}
	
	public static double getNominalDistance(NominalAttribute attribute, NominalParameter param1, NominalParameter param2){
		double res = 0.0;
		ArrayList<String> possibleValue = attribute.getPossibleValue();
		for(String value:possibleValue){
			if(param1.getValue()==null||param2.getValue()==null){
				res+=1.0;
				continue;
			}

			double val1=0.0,val2=0.0;
			if(value.equalsIgnoreCase((String)param1.getValue())){
				val1=1.0;
			}
			if(value.equalsIgnoreCase((String)param2.getValue())){
				val2=1.0;
			}
			res+=Math.abs(val1-val2);
		}
		return res;
	}

}
