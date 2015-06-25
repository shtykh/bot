package shtykh.parrots.what;

/**
 * Created by shtykh on 29/03/15.
 */
public class Phrase extends SomethingWithComments {
	protected final String[] cases;

	public Phrase(String... cases) {
		this.cases = cases;
	}

	@Override
	protected void updateBeforeSaying() {
		//nothing to do
	}

	@Override
	protected String getMainLine() {
		return randomFromArray(cases);
	}
}
