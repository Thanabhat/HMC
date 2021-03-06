package HMC.Container.Data;

import java.util.ArrayList;
import HMC.Container.Attribute.HierarchicalNode;

public class DataEntry {
	public ArrayList<Parameter> parameters;
	public ArrayList<HierarchicalNode> label, rawLabel;
	public ArrayList<HierarchicalNode> predictedLabel;
	
	public DataEntry() {
		// TODO Auto-generated constructor stub
		parameters = new ArrayList<Parameter>();
		label = new ArrayList<HierarchicalNode>();
		predictedLabel = new ArrayList<HierarchicalNode>();
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

	public ArrayList<HierarchicalNode> getRawLabel() {
		return rawLabel;
	}

	public void setRawLabel(ArrayList<HierarchicalNode> rawLabel) {
		this.rawLabel = rawLabel;
	}
	
	public boolean hasLabel(String fullId){
		for(HierarchicalNode node: label){
			if(node.getFullId() == null) {
				if(fullId == null){
					return true;
				}
			} else if(node.getFullId().equalsIgnoreCase(fullId)){
				return true;
			}
		}
		return false;
	}
	
	public void addPredictedLabel(HierarchicalNode node){
		if(!this.hasPredictedLabel(node.getFullId())){
			predictedLabel.add(node);
		}
	}
	
	public boolean hasPredictedLabel(String fullId){
		for(HierarchicalNode node: predictedLabel){
			if(node.getFullId() == null) {
				if(fullId == null){
					return true;
				}
			} else if(node.getFullId().equalsIgnoreCase(fullId)){
				return true;
			}
		}
		return false;
	}
	
	public void clearPredictedLabel(){
		predictedLabel.clear();
	}
	
	public boolean removePredictedLabel(HierarchicalNode node){
		return predictedLabel.remove(node);
	}
}
