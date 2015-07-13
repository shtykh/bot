package shtykh.util.html.form;

import shtykh.util.html.param.FormParameter;
import shtykh.util.html.param.FormParameterSignature;
import shtykh.util.html.param.Parameter;

import java.lang.reflect.Field;
import java.util.*;

import static shtykh.util.html.param.FormParameterType.comment;

/**
 * Created by shtykh on 12/07/15.
 */
public class ActionBuilder {
	private final String action;
	private final Map<Field, FormParameterSignature> signatureMap;

	public ActionBuilder(String action) {
		this.action = action;
		signatureMap = new HashMap<>();
	}
	
	public ActionBuilder addParam(Field field, FormParameterSignature param) {
		signatureMap.put(field, param);
		return this;
	}
	
	public String buildForm(FormMaterial formMaterial) {
		synchronized (formMaterial) {
			FormBuilder builder = new FormBuilder(action);
			addParameters(builder, formMaterial, new ArrayList<>());
			return builder.build();
		}
	}

	private void addParameters(FormBuilder builder, FormMaterial formMaterial, List<FormMaterial> seen) {
		if (!seen.contains(formMaterial)) {
			seen.add(formMaterial);
		} else {
			return;
		}
		Class<? extends FormMaterial> clazz = formMaterial.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (FormMaterial.class.isAssignableFrom(field.getType())) {
				addParameters(builder, get(field, formMaterial, FormMaterial.class), seen);
			}else if (Parameter.class.isAssignableFrom(field.getType())) {
				builder.addParameter(getParameter(field, formMaterial));
			}
		}
	}

	private Parameter getParameter(Field field, FormMaterial formMaterial) {
			Parameter parameter = get(field, formMaterial, Parameter.class);
			if (parameter instanceof FormParameter) {
				FormParameterSignature sign = getSignFor(field);
				((FormParameter) parameter).applySignature(sign);
			}
			return parameter;
	}

	private <T> T get(Field field, Object object, Class<T> fieldClazz) {
		try {
			field.setAccessible(true);
			T t = fieldClazz.cast(field.get(object));
			field.setAccessible(false);
			return t;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private FormParameterSignature getSignFor(Field field) {
		FormParameterSignature sign = signatureMap.get(field);
		if (sign == null) {
			sign = new FormParameterSignature(field.getName(), comment);
		}
		return sign;
	}
}
