package shtykh.quedit.numerator;

/**
 * Created by shtykh on 01/10/15.
 */
public class NaturalNumerator implements Numerator {
	private final int first;

	public NaturalNumerator(int first) {
		this.first = first;
	}

	@Override
	public String getNumber(int index) {
		return String.valueOf(index + first);
	}

	@Override
	public int getIndex(String number) {
		return Integer.decode(number) - 1;
	}
}
