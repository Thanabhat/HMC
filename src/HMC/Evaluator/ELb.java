package HMC.Evaluator;

import java.util.ArrayList;

import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Data.DataEntry;

public class ELb {
	
	public static void Evaluate(Hierarchical hierarchical, ArrayList<DataEntry> dataEntries){
		Double[] lb = LbMacro.Evaluate(hierarchical);
		Double[] eb = Eb.Evaluate(dataEntries);

		double precision = 0.0, recall = 0.0, f1 = 0.0;
		if(eb[0]+lb[0]>0){
			precision = (2.0*eb[0]*lb[0])/(eb[0]+lb[0]);
		}
		if(eb[1]+lb[1]>0){
			recall = (2.0*eb[1]*lb[1])/(eb[1]+lb[1]);
		}
		if(eb[2]+lb[2]>0){
			f1 = (2.0*eb[2]*lb[2])/(eb[2]+lb[2]);
		}
		System.out.println("Example-label based");
		System.out.println("Precision: "+precision);
		System.out.println("Recall: "+recall);
		System.out.println("F1: "+f1);
		System.out.println();
	}
}
