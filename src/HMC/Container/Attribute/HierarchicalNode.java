package HMC.Container.Attribute;

import java.util.ArrayList;


public class HierarchicalNode {
	public ArrayList<HierarchicalNode> parent;
	public ArrayList<HierarchicalNode> children;
	public String id;
	public String fullId;
	private int level;
	
	public HierarchicalNode() {
		// TODO Auto-generated constructor stub
		parent = new ArrayList<HierarchicalNode>();
		children = new ArrayList<HierarchicalNode>();
	}
	
	public HierarchicalNode(int level) {
		// TODO Auto-generated constructor stub
		parent = new ArrayList<HierarchicalNode>();
		children = new ArrayList<HierarchicalNode>();
		this.level = level;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
