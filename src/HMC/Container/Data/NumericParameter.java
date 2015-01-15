package HMC.Container.Data;

import HMC.Container.Attribute.Attribute;

public class NumericParameter extends Parameter {

	public Double value;

	public NumericParameter(String rawData, Attribute attribute) {
		super(rawData, attribute);
		// TODO Auto-generated constructor stub
		if (rawData.equals("?")) {
			this.setValue(null);
		} else {
			this.setValue(Double.parseDouble(rawData));
		}
	}

	@Override
	public Double getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		this.value = (Double) value;
	}

}
