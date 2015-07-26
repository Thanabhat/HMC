package HMC.Evaluator;

import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;

public class LbMicro {

	public static Double[] Evaluate(Hierarchical hierarchical) {
		return Evaluate(hierarchical, true);
	}

	public static Double[] Evaluate(Hierarchical hierarchical, boolean printResult) {
		Double[] result = Calc(hierarchical);
		if (printResult) {
			System.out.println("Label-based micro-average");
			System.out.println("Precision: " + result[0]);
			System.out.println("Recall: " + result[1]);
			System.out.println("F1: " + result[2]);
			System.out.println();
		}
		return result;
	}

	public static Double[] Calc(Hierarchical hierarchical) {
		//use trick to pass integer by reference
		int[] tp = new int[] { 0 }, fp = new int[] { 0 }, fn = new int[] { 0 };
		CalcDFS(hierarchical.root, tp, fp, fn);
		double resultF1 = 0, resultPr = 0, resultRe = 0;
		if (tp[0] + fp[0] > 0) {
			resultPr = 1.0 * tp[0] / (tp[0] + fp[0]);
		}
		if (tp[0] + fn[0] > 0) {
			resultRe = 1.0 * tp[0] / (tp[0] + fn[0]);
		}
		if (resultPr + resultRe > 0) {
			resultF1 = 2.0 * resultPr * resultRe / (resultPr + resultRe);
		}
		return new Double[] { resultPr, resultRe, resultF1 };
	}

	private static void CalcDFS(HierarchicalNode node, int[] tp, int[] fp, int[] fn) {
		if (null != node.getFullId()) {
			tp[0] += node.getTP();
			fp[0] += node.getFP();
			fn[0] += node.getFN();
		}
		for (HierarchicalNode childNode : node.children) {
			CalcDFS(childNode, tp, fp, fn);
		}
	}
}
