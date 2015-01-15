package HMC.Container.Data;

import java.util.ArrayList;

import HMC.Container.Attribute.Attribute;
import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;

public class HierarchicalParameter extends Parameter {
	
	public ArrayList<HierarchicalNode> value;
	
	public HierarchicalParameter(String rawData, Attribute attribute) {
		// TODO Auto-generated constructor stub
		super(rawData, attribute);
		String[] splited = rawData.split("@");
		this.value = new ArrayList<HierarchicalNode>();
		for(String str : splited){
			HierarchicalNode node = ((Hierarchical)attribute).hierarchicalMapping.get(str);
			this.addHierarchicalNode(node);
		}
	}

	@Override
	public ArrayList<HierarchicalNode> getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		this.value = (ArrayList<HierarchicalNode>)value;
	}
	
	public void addHierarchicalNode(HierarchicalNode h){
		this.value.add(h);
	}

}
