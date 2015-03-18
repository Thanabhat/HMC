package HMC.Evaluator;

import java.text.DecimalFormat;
import java.util.ArrayList;

import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;

public class LbMacro {

	public static Double[] Evaluate(Hierarchical hierarchical){
		return Evaluate(hierarchical, true);
	}
	
	public static Double[] Evaluate(Hierarchical hierarchical, boolean printResult){
//		double precision = Precision(hierarchical);
//		double recall = Recall(hierarchical);
//		double f1 = F1(hierarchical);
		Double[] result = Calc(hierarchical);
		if(printResult){
			System.out.println("Label-based macro-average");
			System.out.println("Precision: "+result[0]);
			System.out.println("Recall: "+result[1]);
			System.out.println("F1: "+result[2]);
			System.out.println();
		}
		return result;
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
	


	public static Double[] Calc(Hierarchical hierarchical){
		ArrayList<Double> pr = new ArrayList<Double>();
		ArrayList<Double> re = new ArrayList<Double>();
		ArrayList<Double> f1 = new ArrayList<Double>();
		CalcDFS(hierarchical.root, pr, re, f1);
		double resultF1 = 0, resultPr = 0, resultRe = 0;
		if(pr.size()>0){
			for(double _pr : pr){
				resultPr+=_pr;
			}
			resultPr/=pr.size();
		}
		if(re.size()>0){
			for(double _re : re){
				resultRe+=_re;
			}
			resultRe/=re.size();
		}
		if(f1.size()>0){
			for(double _f1 : f1){
				resultF1+=_f1;
			}
			resultF1/=f1.size();
		}
		return new Double[]{resultPr,resultRe,resultF1};
	}
	
	private static void CalcDFS(HierarchicalNode node, ArrayList<Double> pr, ArrayList<Double> re, ArrayList<Double> f1){
		double _re=0.0,_pr=0.0,_f1=0.0;
		boolean _hasRe = false, _hasPr = false, _hasF1 = false;
		if(null!=node.getFullId()&&node.member.size()>=0){
			if(node.getTP()+node.getFP()>1E-7){
				_pr=(1.0*node.getTP())/(node.getTP()+node.getFP());
				pr.add(_pr);
				_hasPr = true;
			}
			if(node.getTP()+node.getFN()>1E-7){
				_re=(1.0*node.getTP())/(node.getTP()+node.getFN());
				re.add(_re);
				_hasRe = true;
			}
			if(_pr+_re>1E-7){
				_f1=(2.0*_pr*_re)/(_pr+_re);
				f1.add(_f1);
				_hasF1 = true;
			}
			if(_hasPr&&_hasRe&&_hasF1){
				pr.add(_pr);
				re.add(_re);
				f1.add(_f1);
//				System.out.println(node.getTP()+", "+node.getFP()+", "+node.getFN()+", \t"+_pr+", "+_re+", "+_f1+", \t"+_hasPr+", "+_hasRe+", "+_hasF1);
			}
		}
		for(HierarchicalNode childNode:node.children){
			CalcDFS(childNode, pr, re, f1);
		}
	}
}
