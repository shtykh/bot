package shtykh.util.html.param;

import shtykh.util.StringSerializer;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static shtykh.util.html.param.FormParameterType.text;

/**
 * Created by shtykh on 10/07/15.
 */
public class FormParameter<T> extends Parameter<T> {
	private final StringSerializer<T> serializer;
	private FormParameterType type;
	private Set<FormParameterType> allowedTypes;

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

	protected void allowTypes(FormParameterType... types) {
		if (allowedTypes == null) {
			allowedTypes = new TreeSet<>();
		}
		Collections.addAll(allowedTypes, types);
	}
	
	private boolean isAllowed(FormParameterType type) {
		return allowedTypes == null 
				|| allowedTypes.isEmpty() 
				|| allowedTypes.contains(type);
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


	public void applySignature(FormParameterSignature sign) {
		FormParameterType type = sign.getType();
		if (! isAllowed(type)) {
			throw new RuntimeException();
		}
		setName(sign.getName());
		this.type = sign.getType();
	}

}
