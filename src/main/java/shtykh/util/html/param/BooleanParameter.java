package shtykh.util.html.param;

import static shtykh.util.html.param.FormParameterType.*;

/**
 * Created by shtykh on 14/07/15.
 */
public class BooleanParameter extends FormParameter<Boolean> {
	public BooleanParameter(String name, Boolean value) {
		super(name, value, Boolean.class, checkbox);
		allowTypes(checkbox, text, hidden, comment);
	}

	@Override
	public FormParameterType getType() {
		return super.getType();
	}
}
