package HMC.Container.Data;

import HMC.Container.Attribute.Attribute;

public abstract class Parameter {
	public String rawData;
	public Attribute attribute;

	public Parameter(String rawData, Attribute attribute){
		this.setRawData(rawData);
		this.setAttribute(attribute);
	}
	
	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public abstract Object getValue();

	public abstract void setValue(Object value);

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

}
