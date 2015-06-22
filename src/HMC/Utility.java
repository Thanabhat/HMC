package HMC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import HMC.Container.HMCDataContainer;
import HMC.Container.Attribute.Attribute;
import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.NumericParameter;

public class Utility {
	public static boolean isMandatoryLeafNode(Hierarchical hierarchical, ArrayList<DataEntry> dataEntries){
		for(DataEntry dataEntry: dataEntries){
			for(HierarchicalNode node:dataEntry.label){
				if(!node.isLeaf()){
//					System.out.println(node.getFullId());
					return false;
				}
			}
		}
		return true;
	}
	
	public static void assignClusPredictionToContainer(HMCDataContainer container, HMCDataContainer prediction, double treshold){
		Hierarchical hierarchical = container.hierarchical;
		for(int i=0;i<prediction.dataEntries.size();i++){
			DataEntry clusDataEntry = prediction.dataEntries.get(i);
			for(int j=0;j<prediction.attributes.size();j++){
				Attribute attribute = prediction.attributes.get(j);
				if(attribute.getName().startsWith("Original-p-")){
					if(((Double)(clusDataEntry.parameters.get(j).getValue()))>treshold){
						String label = attribute.getName().substring(11);
						HierarchicalNode node = hierarchical.hierarchicalMapping.get(label);
						DataEntry dataEntry = container.dataEntries.get(i);
						dataEntry.addPredictedLabel(node);
						node.addPredictedMember(dataEntry);
					}
				}
			}
		}
	}
	
	public static void numericalNormalizer(HMCDataContainer[] data){
		Double[] maxNumericParameter = new Double[data[0].attributes.size()];
		Double[] minNumericParameter = new Double[data[0].attributes.size()];
		java.util.Arrays.fill(maxNumericParameter, -1000000.0);
		java.util.Arrays.fill(minNumericParameter, 1000000.0);
		 
		for(int i_data=0;i_data<data.length;i_data++){
			for (int i = 0; i < data[i_data].dataEntries.size(); i++) {
				 for(int j=0;j<maxNumericParameter.length;j++){
					 Object parameter = data[i_data].dataEntries.get(i).parameters.get(j);
					 if(parameter instanceof NumericParameter){
						 Double value = ((NumericParameter) parameter).getValue();
						 if(value!=null){
							 maxNumericParameter[j] = maxNumericParameter[j]>value?maxNumericParameter[j]:value;
							 minNumericParameter[j] = minNumericParameter[j]<value?minNumericParameter[j]:value;
						 }
					 }
				 }
			}
		}

		for(int i_data=0;i_data<data.length;i_data++){
			for (int i = 0; i < data[i_data].dataEntries.size(); i++) {
				 for(int j=0;j<maxNumericParameter.length;j++){
					 Object parameter = data[i_data].dataEntries.get(i).parameters.get(j);
					 if(parameter instanceof NumericParameter){
						 Double value = ((NumericParameter) parameter).getValue();
						 if(value!=null){
							 if(maxNumericParameter[j]!=minNumericParameter[j]){
								 ((NumericParameter) parameter).setValue((value-minNumericParameter[j])/(maxNumericParameter[j]-minNumericParameter[j])-0.5);
							 }else{
								 ((NumericParameter) parameter).setValue(0.0);
							 }
						 }
					 }
				 }
			}
		}
	}
	
	public static void correctHierarchical(ArrayList<DataEntry> dataEntries){
		for(DataEntry dataEntry : dataEntries){
			correctHierarchical(dataEntry);
		}
	}
	
	private static void correctHierarchical(DataEntry dataEntry){
		correctHierarchicalByAddAndRemove(dataEntry);
	}
	
	private static void correctHierarchicalByRemove(DataEntry dataEntry){
		Set<HierarchicalNode> nodeToRemove = new HashSet<HierarchicalNode>();
		for(HierarchicalNode node : dataEntry.predictedLabel){
			Set<HierarchicalNode> allAncestor = Hierarchical.getAllAncestor(node);
			boolean isValid = true;
			for(HierarchicalNode ancestor: allAncestor){
				if(!dataEntry.hasPredictedLabel(ancestor.getFullId())){
					isValid = false;
				}
			}
			if(!isValid){
				nodeToRemove.add(node);
			}
		}
		for(HierarchicalNode node:nodeToRemove){
			dataEntry.removePredictedLabel(node);
			node.removePredictedMember(dataEntry);
		}
	}
	
	private static void correctHierarchicalByAdd(DataEntry dataEntry){
		Set<HierarchicalNode> nodeToAdd = new HashSet<HierarchicalNode>();
		for(HierarchicalNode node : dataEntry.predictedLabel){
			Set<HierarchicalNode> allAncestor = Hierarchical.getAllAncestor(node);
			for(HierarchicalNode ancestor: allAncestor){
				if(!dataEntry.hasPredictedLabel(ancestor.getFullId())){
					nodeToAdd.add(ancestor);
				}
			}
		}
		for(HierarchicalNode node:nodeToAdd){
			dataEntry.addPredictedLabel(node);
			node.addPredictedMember(dataEntry);
		}
	}
	
	private static void correctHierarchicalByAddAndRemove(DataEntry dataEntry){
		Set<HierarchicalNode> nodeToRemove = new HashSet<HierarchicalNode>();
		Set<HierarchicalNode> nodeToAdd = new HashSet<HierarchicalNode>();
		for(HierarchicalNode node : dataEntry.predictedLabel){
			Set<HierarchicalNode> allAncestor = Hierarchical.getAllAncestor(node);
			int count = 0;
			for(HierarchicalNode ancestor: allAncestor){
				if(!dataEntry.hasPredictedLabel(ancestor.getFullId())){
					count--;
				}else{
					count++;
				}
			}
			if(count<=0){
				nodeToRemove.add(node);
			}else{
				for(HierarchicalNode ancestor: allAncestor){
					nodeToAdd.add(ancestor);
				}
			}
		}
		for(HierarchicalNode node:nodeToRemove){
			dataEntry.removePredictedLabel(node);
			node.removePredictedMember(dataEntry);
		}
		for(HierarchicalNode node:nodeToAdd){
			dataEntry.addPredictedLabel(node);
			node.addPredictedMember(dataEntry);
		}
	}
}
