package shtykh.parrots.what;

import org.apache.commons.lang3.StringUtils;

import static shtykh.util.Util.random;

/**
 * Created by shtykh on 12/07/15.
 */
public class CSV {
	private String value;

	public CSV(String... array) {
		setArray(array);
	}

	public CSV setArray(String... array) {
		StringBuilder sb = new StringBuilder();
		for (String s : array) {
			sb.append(s)
					.append("\n");
		}
		int lastCommaIndex = sb.lastIndexOf("\n");
		if (lastCommaIndex > 0) {
			value = sb.substring(0, sb.lastIndexOf("\n"));
		}
		return this;
	}

	public String[] asArray() {
		return value.split("\n");
	}

	public static CSV fromArray(String... array) {
		CSV csv = new CSV();
		return csv.setArray(array);
	}
	
	public String toString() {
		return value == null ? "" : value;
	}

	public void setString(String value) {
		this.value = value;
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(value);
	}

	public String getRandom() {
		return randomFromArray(asArray());
	}
	
	private String randomFromArray(String[] array) {
		if (array.length == 0) {
			return "";
		} else {
			return array[random.nextInt(array.length)];
		}
	}
}
