package shtykh.util.html.form.build;

import shtykh.util.html.TagBuilder;
import shtykh.util.html.form.material.BooleanParameterMaterial;
import shtykh.util.html.form.param.FormParameter;
import shtykh.util.html.form.param.FormParameterType;
import shtykh.util.html.param.Parameter;

import java.util.ArrayList;
import java.util.List;

import static shtykh.util.html.TagBuilder.tag;
import static shtykh.util.html.form.param.FormParameterType.*;


/**
 * Created by shtykh on 10/07/15.
 */
public class FormBuilder {
	private String action;
	private List<Parameter> members = new ArrayList<>();

	public FormBuilder(String action) {
		this.action = action;
	}

	public FormBuilder addMember(Parameter parameter) {
		members.add(parameter);
		return this;
	}

	private String formFor(FormParameter parameter) {
		String form = "";
		switch(parameter.getType()) {
			case checkbox:
				if(parameter.getMaterial() instanceof BooleanParameterMaterial) {
					form = checkbox(parameter);
				}
				break;
			case textarea:
				form = textarea(parameter);
			break;
			default:
				form = input(parameter);
			break;
		}
		return getLabel(parameter) + form;
	}

	private String input(FormParameter parameter) {
		return tag("input")
						.params(
								new Parameter<>("type", parameter.getType()),
								new Parameter<>("name", parameter.getName()),
								new Parameter<>("value", parameter.getValueString()))
				.toString();
	}

	private String textarea(FormParameter parameter) {
		return "<br>" + tag("textarea").params(
				new Parameter<>("rows", "4"),
				new Parameter<>("cols", "50"),
				new Parameter<>("name", parameter.getName())).build(parameter.getValueString());
	}

	private String checkbox(FormParameter<Boolean> parameter) {
		TagBuilder setTrueInput = tag("input")
				.params(
						new Parameter<>("type", checkbox),
						new Parameter<>("name", parameter.getName()),
						new Parameter<>("value", "true")
				);
		if (parameter.getMaterial().get()) {
			setTrueInput.params(new Parameter<>("checked", "true"));
		}
		return setTrueInput.toString();
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
			if (member instanceof FormParameter) {
				FormParameter parameter = (FormParameter) member;
				if (parameter.getType().isComment()) {
					sb.append(tag("p").build(parameter));
				} else {
					sb.append(tag("p").build(formFor(parameter)));
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
