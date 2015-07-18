package shtykh.util.html.form;

import shtykh.util.StringSerializer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static shtykh.util.html.form.FormParameterType.*;

/**
 * Created by shtykh on 18/07/15.
 */
public class CommentMaterial<T> extends FormParameterMaterial<T> {
	public CommentMaterial(T value, Class<T> clazz) {
		super(value, clazz);
		allowTypes(comment);
	}

	@Override
	public void setValueString(String s) {
		throw new NotImplementedException();
	}

	@Override
	public String getValueString() {
		return value.toString();
	}

	@Override
	protected StringSerializer<T> getSerializer(Class<T> clazz) {
		return null;
	}
}
