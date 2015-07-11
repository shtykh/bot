package shtykh.util.html.param;

import static shtykh.util.html.param.FormParameterType.*;

/**
 * Created by shtykh on 10/07/15.
 */
public class TextParameter<T> extends FormParameter<T> {
	public TextParameter(String name, T value, Class<T> clazz) {
		super(name, value, clazz, text);
	}
}
