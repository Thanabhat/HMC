package HMC.Evaluator;

import java.text.DecimalFormat;
import java.util.ArrayList;

import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;

public class LbMacro {
	public static Double[] Evaluate(Hierarchical hierarchical){
		double precision = Precision(hierarchical);
		double recall = Recall(hierarchical);
		double f1 = F1(hierarchical);
		System.out.println("Label-based macro-average");
		System.out.println("Precision: "+precision);
		System.out.println("Recall: "+recall);
		System.out.println("F1: "+f1);
		System.out.println();
		return new Double[]{precision,recall,f1};
	}
	
	public static double Precision(Hierarchical hierarchical){
		ArrayList<Double> precisions = new ArrayList<Double>();
		DFSPrecision(hierarchical.root, precisions);
		if(precisions.size()==0){
			return 0.0;
		}
		double result = 0;
		for(double pr : precisions){
			result+=pr;
		}
		result/=precisions.size();
		return result;
	}
	
	private static void DFSPrecision(HierarchicalNode node, ArrayList<Double> precisions){
		if(null!=node.getFullId()){
			if(node.getTP()+node.getFP()>0){
				precisions.add((1.0*node.getTP())/(node.getTP()+node.getFP()));
			}
		}
		for(HierarchicalNode childNode:node.children){
			DFSPrecision(childNode, precisions);
		}
	}

	public static double Recall(Hierarchical hierarchical){
		ArrayList<Double> recalls = new ArrayList<Double>();
		DFSRecall(hierarchical.root, recalls);
		if(recalls.size()==0){
			return 0.0;
		}
		double result = 0;
		for(double re : recalls){
			result+=re;
		}
		result/=recalls.size();
		return result;
	}
	
	private static void DFSRecall(HierarchicalNode node, ArrayList<Double> recalls){
		if(null!=node.getFullId()){
			if(node.getTP()+node.getFN()>0){
				recalls.add((1.0*node.getTP())/(node.getTP()+node.getFN()));
			}
		}
		for(HierarchicalNode childNode:node.children){
			DFSRecall(childNode, recalls);
		}
	}

	public static double F1(Hierarchical hierarchical){
		ArrayList<Double> f1s = new ArrayList<Double>();
		DFSF1(hierarchical.root, f1s);
		if(f1s.size()==0){
			return 0.0;
		}
		double result = 0;
		for(double f1 : f1s){
			result+=f1;
		}
		result/=f1s.size();
		return result;
	}
	
	private static void DFSF1(HierarchicalNode node, ArrayList<Double> f1){
		double re=0.0,pr=0.0;
		if(null!=node.getFullId()){
			if(node.getTP()+node.getFP()>0){
				pr=(1.0*node.getTP())/(node.getTP()+node.getFP());
			}
			if(node.getTP()+node.getFN()>0){
				re=(1.0*node.getTP())/(node.getTP()+node.getFN());
			}
			if(pr+re>0.0){
				f1.add((2.0*pr*re)/(pr+re));
			}
		}
//		if(pr>1E-5||re>1E-5){
//			DecimalFormat df = new DecimalFormat();
//			df.setMaximumFractionDigits(4);
//			df.setMinimumFractionDigits(4);
//			System.out.println(node.getFullId()+"\t("+df.format(pr)+","+df.format(re)+","+df.format((2.0*pr*re)/(pr+re))+")");
//		}
		for(HierarchicalNode childNode:node.children){
			DFSF1(childNode, f1);
		}
	}
}
