package shtykh.parrots.what;

import shtykh.util.html.param.FormParameter;

import static shtykh.parrots.what.CSV.fromArray;

/**
 * Created by shtykh on 29/03/15.
 */
public class Phrase extends SomethingWithComments {
	protected final FormParameter<CSV> cases
			= new FormParameter("cases", new CSV(), CSV.class);

	public Phrase(String... cases) {
		this.cases.setValue(fromArray(cases));
	}

	@Override
	protected void updateBeforeSaying() {
		//nothing to do
	}

	@Override
	protected String getMainLine() {
		return cases.getValue().getRandom();
	}

	@Override
	public void edit(String before, String cases, String after) {
		super.edit(before, cases, after);
		if (cases != null) {
			setCases(cases);
		}
	}
	
	public void setCases(String cases) {
		this.cases.setValue(cases);
	}
}
