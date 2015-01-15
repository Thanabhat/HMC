package HMC.Container.Attribute;

import java.util.ArrayList;


public class HierarchicalNode {
	public ArrayList<HierarchicalNode> parent;
	public ArrayList<HierarchicalNode> children;
	public String id;
	public String fullId;
	
	public HierarchicalNode() {
		// TODO Auto-generated constructor stub
		parent = new ArrayList<HierarchicalNode>();
		children = new ArrayList<HierarchicalNode>();
	}
	
	public void addParent(HierarchicalNode h){
		parent.add(h);
	}
	
	public void addChildren(HierarchicalNode h){
		children.add(h);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	
}
