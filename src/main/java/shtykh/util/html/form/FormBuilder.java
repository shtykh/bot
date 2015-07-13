package shtykh.util.html.form;

import shtykh.util.html.TagBuilder;
import shtykh.util.html.param.BooleanParameter;
import shtykh.util.html.param.FormParameter;
import shtykh.util.html.param.FormParameterType;
import shtykh.util.html.param.Parameter;

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
	
	public FormBuilder(String action) {
		this.action = action;
	}

	public FormBuilder addParameter(Parameter parameter) {
		members.add(parameter);
		return this;
	}

	private String input(FormParameter parameter) {
		if (parameter instanceof BooleanParameter 
				&& parameter.getType().equals(checkbox)) {
			return checkbox((BooleanParameter) parameter);
		}
		return getLabel(parameter) + 
				tag("input")
				.params(
						new Parameter<>("type", parameter.getType()),
						new Parameter<>("name", parameter.getName()),
						new Parameter<>("value", parameter.getValueString()));
	}

	private String checkbox(BooleanParameter parameter) {
		TagBuilder setTrueInput = tag("input")
				.params(
						new Parameter<>("type", checkbox),
						new Parameter<>("name", parameter.getName()),
						new Parameter<>("value", "true")
				);
		if (parameter.getValue()) {
			setTrueInput.params(new Parameter<>("checked", "true"));
		}
		return getLabel(parameter) + setTrueInput;
	}

	private String input(FormParameterType type, String value) {
		return tag("input")
				.params(
						new Parameter<>("type", type),
						new Parameter<>("value", value)
				).toString();
	}

	public String build(){
		StringBuilder sb = new StringBuilder();
		sb.append(tag("legend").build("Form for " + action));
		sb.append(input(reset, "Reset"));
		for(Parameter member: members) {
			System.out.println(member);
			if (member instanceof FormParameter) {
				FormParameter parameter = (FormParameter) member;
				if (parameter.getType().isComment()) {
					sb.append(tag("p").build(parameter));
				} else {
					sb.append(tag("p").build(input(parameter)));
				}
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
		switch (member.getType()) {
			case hidden:
				return "";
			case checkbox:
				return member.getName() + " : set \"true\"";
			default:
				return member.getName() + " : ";
		}
	}
}
