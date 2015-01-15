package HMC.Container;

import java.util.ArrayList;

import HMC.Container.Attribute.*;
import HMC.Container.Data.DataEntry;

public class HMCDataContainer {
	public String relation;
	public ArrayList<Attribute> attributes;
	public Hierarchical hierarchical;
	public ArrayList<DataEntry> dataEntries;

	public HMCDataContainer(){
		attributes = new ArrayList<Attribute>();
		dataEntries = new ArrayList<DataEntry>();
	}
	
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Iterable<Attribute> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(Attribute attr){
		attributes.add(attr);
	}

	public Hierarchical getHierarchical() {
		return hierarchical;
	}

	public void setHierarchical(Hierarchical hierarchical) {
		this.hierarchical = hierarchical;
	}
	
	public void addDataEntry(DataEntry dataEntry){
		dataEntries.add(dataEntry);
	}
}
