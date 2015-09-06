package HMC.Evaluator;

import java.util.ArrayList;

import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Data.DataEntry;

public class ELb {

	public static Double[] Evaluate(Hierarchical hierarchical, ArrayList<DataEntry> dataEntries) {
		return Evaluate(hierarchical, dataEntries, true);
	}

	public static Double[] Evaluate(Hierarchical hierarchical, ArrayList<DataEntry> dataEntries, boolean printDetailResult) {
		// Double[] lb = LbMacro.Evaluate(hierarchical, printDetailResult);
		Double[] lb = LbMicro.Evaluate(hierarchical, printDetailResult);
		Double[] eb = Eb.Evaluate(dataEntries, printDetailResult);

		double precision = 0.0, recall = 0.0, f1 = 0.0;
		if (eb[0] + lb[0] > 0) {
			precision = (2.0 * eb[0] * lb[0]) / (eb[0] + lb[0]);
		}
		if (eb[1] + lb[1] > 0) {
			recall = (2.0 * eb[1] * lb[1]) / (eb[1] + lb[1]);
		}
		if (eb[2] + lb[2] > 0) {
			f1 = (2.0 * eb[2] * lb[2]) / (eb[2] + lb[2]);
		}
		if(printDetailResult){
			System.out.println("Example-label based");
			System.out.println("Precision: " + precision);
			System.out.println("Recall: " + recall);
			System.out.println("F1: " + f1);
			System.out.println();
		}
		return new Double[]{precision,recall,f1};
	}
}
