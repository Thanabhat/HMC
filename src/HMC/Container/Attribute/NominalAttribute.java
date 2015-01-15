package HMC.Container.Attribute;

import java.util.ArrayList;

public class NominalAttribute extends Attribute {

	private ArrayList<String> possibleValue;

	public NominalAttribute() {
		// TODO Auto-generated constructor stub
		possibleValue = new ArrayList<String>();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "nominal";
	}

	@Override
	public Object getPossibleValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setPossibleValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPossibleValue(String value) {
		possibleValue.add(value);
	}

	public void addPossibleValue(String[] value) {
		for (String str : value) {
			possibleValue.add(str);
		}
	}

	public int getPossibleValueSize(){
		return possibleValue.size();
	}
}
