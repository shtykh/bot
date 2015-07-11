package shtykh.util.html.param;

/**
 * Created by shtykh on 25/06/15.
 */
public class Parameter<T> {
	protected String name;
	protected T value;

	public Parameter(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name + "=\"" + value + "\"" ;
	}

	public String getValueString() {
		return value.toString();
	}

	public void setName(String name) {
		this.name = name;
	}
}