package shtykh.parrots.what;

/**
 * Created by shtykh on 29/03/15.
 */
public class Phrase extends SomethingWithComments {
	protected final CommaSeparatedValues cases = new CommaSeparatedValues();

	public Phrase(String... cases) {
		this.cases.fromArray(cases);
	}

	@Override
	protected void updateBeforeSaying() {
		//nothing to do
	}

	@Override
	protected String getMainLine() {
		return cases.getRandom();
	}
}
