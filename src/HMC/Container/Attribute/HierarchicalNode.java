package HMC.Container.Attribute;

import java.util.ArrayList;

import HMC.Container.Data.DataEntry;


public class HierarchicalNode {
	public ArrayList<HierarchicalNode> parent;
	public ArrayList<HierarchicalNode> children;
	public String id;
	public String fullId;
	private int level;
	public ArrayList<DataEntry> member;
	public ArrayList<DataEntry> predictedMember;
	private int TP;
	private int FP;
	private int FN;

	public HierarchicalNode() {
		// TODO Auto-generated constructor stub
		parent = new ArrayList<HierarchicalNode>();
		children = new ArrayList<HierarchicalNode>();
		
		member = new ArrayList<DataEntry>();
		predictedMember = new ArrayList<DataEntry>();
		TP=0;
		FP=0;
		FN=0;
	}
	
	public HierarchicalNode(int level) {
		// TODO Auto-generated constructor stub
		this();
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
	
	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	public void addMember(DataEntry dataEntry){
		if(!this.hasMember(dataEntry)){
			member.add(dataEntry);	
		}
	}
	
	public boolean hasMember(DataEntry dataEntry){
		return member.contains(dataEntry);
	}
	
	public boolean removeMember(DataEntry dataEntry){
		return member.remove(dataEntry);
	}
	
	public void addPredictedMember(DataEntry dataEntry){
		if(!this.hasPredictedMember(dataEntry)){
			predictedMember.add(dataEntry);
		}
	}
	
	public boolean hasPredictedMember(DataEntry dataEntry){
		return predictedMember.contains(dataEntry);
	}

	public int getTP() {
		return TP;
	}

	public void setTP(int tP) {
		TP = tP;
	}

	public int getFP() {
		return FP;
	}

	public void setFP(int fP) {
		FP = fP;
	}

	public int getFN() {
		return FN;
	}

	public void setFN(int fN) {
		FN = fN;
	}
	
	public void clearPredictedMember(){
		predictedMember.clear();
	}
	
	public boolean removePredictedMember(DataEntry dataEntry){
		return predictedMember.remove(dataEntry);
	}
}
