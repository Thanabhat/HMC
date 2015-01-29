package HMC.Evaluator;

import java.util.ArrayList;
import java.util.Collections;

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
	
	public static void printResult(ArrayList<DataEntry> dataEntries){
		for(DataEntry dataEntry:dataEntries){
			printResult(dataEntry);
			System.out.print("\n");
		}
	}
	
	public static void printResult(DataEntry dataEntry){
		System.out.print("Real Label:");
		ArrayList<String> label = new ArrayList<String>();
		for(HierarchicalNode node:dataEntry.label){
			if(node.getFullId()==null){
				label.add("root");
			}else{
				label.add(node.getFullId());
			}
		}
		Collections.sort(label);
		for(String str:label){
			System.out.print(", ");
			System.out.print(str);
		}
		
		System.out.print("\n");
		
		System.out.print("Real Label:");
		ArrayList<String> predict = new ArrayList<String>();
		for(HierarchicalNode node:dataEntry.predictedLabel){
			if(node.getFullId()==null){
				predict.add("root");
			}else{
				predict.add(node.getFullId());
			}
		}
		Collections.sort(predict);
		for(String str:predict){
			System.out.print(", ");
			System.out.print(str);
		}

		System.out.print("\n");
	}
}
