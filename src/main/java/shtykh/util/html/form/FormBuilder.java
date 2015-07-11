package shtykh.util.html.form;

import shtykh.util.html.param.FormParameter;
import shtykh.util.html.param.FormParameterType;
import shtykh.util.html.param.Parameter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static shtykh.util.html.TagBuilder.tag;
import static shtykh.util.html.param.FormParameterType.*;


/**
 * Created by shtykh on 10/07/15.
 */
public class FormBuilder {
	private String action;
	private List<Parameter> members = new ArrayList<>();
	
	private FormBuilder(String action) {
		this.action = action;
	}

	public FormBuilder addMember(Parameter parameter) {
		members.add(parameter);
		return this;
	}

	private String input(FormParameter parameter) {
		return getLabel(parameter) + 
				tag("input")
				.params(
						new Parameter<>("type", parameter.getType()),
						new Parameter<>("name", parameter.getName()),
						new Parameter<>("value", parameter.getValueString()));
	}

	private String input(FormParameterType type, String value) {
		return tag("input")
				.params(
						new Parameter<>("type", type),
						new Parameter<>("value", value)
				).toString();
	}

	private String build(){
		StringBuilder sb = new StringBuilder();
		sb.append(tag("legend").build("Form for " + action));
		sb.append(input(reset, "Reset"));
		for(Parameter member: members) {
			System.out.println(member);
			if (member instanceof FormParameter) {
				FormParameter parameter = (FormParameter) member;
				sb.append(tag("p").build(input(parameter)));
			} else {
				sb.append(tag("p").build(member));
			}
		}
		sb.append(input(submit, "Submit"));
		String fieldset = tag("fieldset").build(sb.toString());
		return tag("form")
				.params(
						new Parameter<>("method", "get"),
						new Parameter<>("action", action))
				.build(fieldset);
	}

	private String getLabel(FormParameter member) {
		return member.getType().equals(hidden) ? "" : member.getName() + " : ";
	}
	
	public static String buildForm(FormMaterial formMaterial, String action) {
		synchronized (formMaterial) {
			formMaterial.renameParametersFor(action);
			FormBuilder builder = new FormBuilder(action);
			Class<? extends FormMaterial> clazz = formMaterial.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				System.out.println(field.getName());
				if (Parameter.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						builder.addMember((Parameter) field.get(formMaterial));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					field.setAccessible(false);
				}
			}
			return builder.build();
		}
	}
}
