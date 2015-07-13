package shtykh.util.html.param;

/**
 * Created by shtykh on 12/07/15.
 */
public class FormParameterSignature {
	private final String name;
	private final FormParameterType type;

	public FormParameterSignature(String name, FormParameterType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public FormParameterType getType() {
		return type;
	}
}
