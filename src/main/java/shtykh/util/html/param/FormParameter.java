package shtykh.util.html.param;

import shtykh.util.StringSerializer;

import static shtykh.util.html.param.FormParameterType.*;

/**
 * Created by shtykh on 10/07/15.
 */
public class FormParameter<T> extends Parameter<T> {
	private final StringSerializer<T> serializer;
	private final FormParameterType type;

	public FormParameter(String name, T value, Class<T> clazz) {
		this(name, value, clazz, text);
	}

	public FormParameter(String name, T value, Class<T> clazz, FormParameterType type) {
		super(name, value);
		this.type = type;
		this.serializer = StringSerializer.getForClass(clazz);
		if (serializer == null) {
			throw new NullPointerException("serializer for " + clazz.getCanonicalName());
		}
	}

	@Override
	public String getValueString() {
		return serializer.toString(value);
	}

	@Override
	public String toString() {
		return name + "=\"" + 
			getValueString() + "\"" ;
	}

	public void setValue(String s) {
		value = serializer.fromString(s);
	}

	public FormParameterType getType() {
		return type;
	}

}
