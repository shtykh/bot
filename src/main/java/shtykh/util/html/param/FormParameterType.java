package shtykh.util.html.param;

/**
 * Created by shtykh on 10/07/15.
 */
public enum FormParameterType {
	text,
	submit,
	button,
	checkbox,
	file,
	hidden,
	image,
	password,
	radio,
	reset,
	color,
	date,
	datetime,
	email,
	number,
	range,
	search,
	tel,
	time,
	url,
	month,
	week,
	datetime_local;

	@Override
	public String toString() {
		return name().replace("_", "-");
	}


}
