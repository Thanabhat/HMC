package HMC.Container.Data;

import java.util.ArrayList;
import HMC.Container.Attribute.HierarchicalNode;

public class DataEntry {
	public ArrayList<Parameter> parameters;
	public ArrayList<HierarchicalNode> label;
	
	public DataEntry() {
		// TODO Auto-generated constructor stub
		parameters = new ArrayList<Parameter>();
		label = new ArrayList<HierarchicalNode>();
	}
	
	public void addParameter(Parameter param){
		parameters.add(param);
	}

	public ArrayList<HierarchicalNode> getLabel() {
		return label;
	}

	public void setLabel(ArrayList<HierarchicalNode> label) {
		this.label = label;
	}
}
