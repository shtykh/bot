package shtykh.util.html.param;

/**
 * Created by shtykh on 10/07/15.
 */
public class Comment<T> extends Parameter<T> {
	public Comment(String name, T value) {
		super(name, value);
	}

	@Override
	public String toString() {
		return name + " : " + value;
	}
}
