package HMC.Evaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Data.DataEntry;

public class Eb {
	public static Double[] Evaluate(ArrayList<DataEntry> dataEntries){

		double sumPrecision=0.0,sumRecal=0.0,sumF1=0.0;
		
		for(DataEntry dataEntry: dataEntries){
			HashSet<HierarchicalNode> P = new HashSet<HierarchicalNode>();
			HashSet<HierarchicalNode> T = new HashSet<HierarchicalNode>();
			for(HierarchicalNode node:dataEntry.label){
				T.addAll(getAllAncestor(node));
			}
			for(HierarchicalNode node:dataEntry.predictedLabel){
				P.addAll(getAllAncestor(node));
			}
			Set<HierarchicalNode> intersection = new HashSet<HierarchicalNode>(T);
			intersection.retainAll(P);
			double precision = 0.0;
			if(P.size()>0){
				precision = 1.0*intersection.size()/P.size();
			}
			double recall = 0.0;
			if(T.size()>0){
				recall = 1.0*intersection.size()/T.size();
			}
			double f1 = 0.0;
			if(precision+recall>0){
				f1 = (2.0*precision*recall)/(precision+recall);
			}
//			System.out.println("Precision: "+precision);
//			System.out.println("Recall: "+recall);
//			System.out.println("F1: "+f1);
			sumPrecision+=precision;
			sumRecal+=recall;
			sumF1+=f1;
		}
		
		sumPrecision/=dataEntries.size();
		sumRecal/=dataEntries.size();
		sumF1/=dataEntries.size();
		System.out.println("Example-based");
		System.out.println("Precision: "+sumPrecision);
		System.out.println("Recall: "+sumRecal);
		System.out.println("F1: "+sumF1);
		System.out.println();
		
		return new Double[]{sumPrecision,sumRecal,sumF1};
	}
	
	private static Set<HierarchicalNode> getAllAncestor(HierarchicalNode node){
		Set<HierarchicalNode> res = new HashSet<HierarchicalNode>();
		getAllAncestor(node, res);
		return res;
	}
	
	private static void getAllAncestor(HierarchicalNode node, Set<HierarchicalNode> s){
		s.add(node);
		for(HierarchicalNode parent: node.parent){
			getAllAncestor(parent, s);
		}
	}
}
