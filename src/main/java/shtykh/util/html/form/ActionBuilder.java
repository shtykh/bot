package shtykh.util.html.form;

import java.lang.reflect.Field;
import java.util.*;

import static shtykh.util.html.form.FormParameterType.comment;

/**
 * Created by shtykh on 12/07/15.
 */
public class ActionBuilder {
	private final String action;
	private final Map<Field, FormParameterSignature> signatureMap;
	private int signaturesNumber;

	public ActionBuilder(String action) {
		this.action = action;
		this.signaturesNumber = 0;
		signatureMap = new HashMap<>();
	}
	
	public ActionBuilder addParam(Field field, 
								  FormParameterSignature signature) {
		signature.setIndex(signaturesNumber++);
		System.out.println(signature.getName() + " " + signature.getIndex());
		signatureMap.put(field, signature);
		return this;
	}
	
	public String buildForm(FormMaterial formMaterial) {
		synchronized (formMaterial) {
			Map<Integer, FormParameter> parameters = new TreeMap<>();
			addParameters(parameters, formMaterial, new ArrayList<>());
			FormBuilder builder = new FormBuilder(action);
			for (int i = 0; i < signaturesNumber; i++) {
				FormParameter formParameter = parameters.get(i);
				if (formParameter == null) {
					throw new RuntimeException(action + " error: " + i + "th parameter value wasn't found");
				}
				builder.addMember(formParameter);
			}
			return builder.build();
		}
	}

	private void addParameters(Map<Integer, FormParameter> parameters, 
							   FormMaterial formMaterial, 
							   List<FormMaterial> seen) {
		if (!seen.contains(formMaterial)) {
			seen.add(formMaterial);
		} else {
			return;
		}
		Class clazz = formMaterial.getClass();
		while(FormMaterial.class.isAssignableFrom(clazz)) {
			addParameters(clazz, parameters, formMaterial, seen);
			clazz = clazz.getSuperclass();
		}
	}

	private void addParameters(Class<? extends FormMaterial> clazz, 
							   Map<Integer, FormParameter> parameters, 
							   FormMaterial formMaterial, 
							   List<FormMaterial> seen) {
		for (Field field : clazz.getDeclaredFields()) {
			if (FormMaterial.class.isAssignableFrom(field.getType())) {
				addParameters(parameters, get(field, formMaterial, FormMaterial.class), seen);
			}else if (FormParameterMaterial.class.isAssignableFrom(field.getType())) {
				FormParameterSignature sign = getSignFor(field);
				System.out.println("Parameter added: " + sign.getIndex() + getParameter(field, sign, formMaterial));
				parameters.put(sign.getIndex(), getParameter(field, sign, formMaterial));
			}
		}
	}

	private FormParameter getParameter(Field field,FormParameterSignature sign, FormMaterial formMaterial) {
		FormParameterMaterial material = get(field, formMaterial, FormParameterMaterial.class);
		return material.toParameter(sign);
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
			sign.setIndex(signaturesNumber++);
		}
		return sign;
	}
}
