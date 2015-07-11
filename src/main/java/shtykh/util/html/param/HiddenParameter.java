package shtykh.util.html.param;

/**
 * Created by shtykh on 10/07/15.
 */
public class HiddenParameter<T> extends FormParameter<T>{
	public HiddenParameter(String name, T value, Class<T> clazz) {
		super(name, value, clazz, FormParameterType.hidden);
	}
}
