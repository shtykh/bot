package shtykh.util;

/**
 * Created by shtykh on 25/06/15.
 */
public class Parameter {
	private String name;
	private String value;

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
