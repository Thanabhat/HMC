package HMC.Evaluator;

import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Data.DataEntry;

public class Utility {
	public static void PrepareParameter(Hierarchical hierarchical){
		DFS(hierarchical.root);
	}
	
	private static void DFS(HierarchicalNode node){
		int TP=0,FP=0,FN=0;
		for(DataEntry dataEntry: node.predictedMember){
			if(node.member.contains(dataEntry)){
				TP++;
			}else{
				FP++;
			}
		}
		for(DataEntry dataEntry: node.member){
			if(!node.predictedMember.contains(dataEntry)){
				FN++;
			}
		}
		node.setTP(TP);
		node.setFP(FP);
		node.setFN(FN);
		for(HierarchicalNode childNode:node.children){
			DFS(childNode);
		}
	}
	
}
