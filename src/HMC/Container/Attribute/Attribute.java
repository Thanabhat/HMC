package HMC.Container.Attribute;

public abstract class Attribute {
	private String name;

	abstract public String getType();

	public String getName() {
		return name;
	};

	public void setName(String _name) {
		name = _name;
	};

	public abstract Object getPossibleValue();
	public abstract Object setPossibleValue();
}
