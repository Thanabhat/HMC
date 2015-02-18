package HMC;

import java.util.ArrayList;

import HMC.Container.HMCDataContainer;
import HMC.Container.Attribute.Attribute;
import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Data.DataEntry;

public class Utility {
	public static boolean isMandatoryLeafNode(Hierarchical hierarchical, ArrayList<DataEntry> dataEntries){
		for(DataEntry dataEntry: dataEntries){
			for(HierarchicalNode node:dataEntry.label){
				if(!node.isLeaf()){
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
}
