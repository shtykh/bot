package shtykh.parrots.what;

import org.apache.commons.lang3.StringUtils;

import static shtykh.util.Util.random;

/**
 * Created by shtykh on 12/07/15.
 */
public class CommaSeparatedValues {
	private String value;

	public CommaSeparatedValues(String... array) {
		setArray(array);
	}

	public CommaSeparatedValues setArray(String... array) {
		StringBuilder sb = new StringBuilder();
		for (String s : array) {
			sb.append(s)
					.append(", ");
		}
		int lastCommaIndex = sb.lastIndexOf(",");
		if (lastCommaIndex > 0) {
			value = sb.substring(0, sb.lastIndexOf(","));
		}
		return this;
	}

	public String[] asArray() {
		return value.split(",( )*");
	}

	public static CommaSeparatedValues fromArray(String... array) {
		CommaSeparatedValues csv = new CommaSeparatedValues();
		return csv.setArray(array);
	}
	
	public String toString() {
		return value;
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
		return array[random.nextInt(array.length)];
	}
}
