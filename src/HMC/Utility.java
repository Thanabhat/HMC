package HMC;

import java.util.ArrayList;

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
}
