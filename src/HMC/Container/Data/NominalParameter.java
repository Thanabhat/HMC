package HMC.Container.Data;

import HMC.Container.Attribute.Attribute;

public class NominalParameter extends Parameter {

	public String value;

	public NominalParameter(String rawData, Attribute attribute) {
		super(rawData, attribute);
		// TODO Auto-generated constructor stub
		if (rawData.equals("?")) {
			this.setValue(null);
		} else {
			this.setValue(rawData);
		}
	}
	
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		this.value = (String)value;
	}

}
