package shtykh.parrots.what;

import shtykh.util.html.form.FormParameterMaterial;

import static shtykh.parrots.what.CSV.fromArray;

/**
 * Created by shtykh on 29/03/15.
 */
public class Phrase extends SomethingWithComments {
	protected final FormParameterMaterial<CSV> cases
			= new FormParameterMaterial<>(new CSV(), CSV.class);

	public Phrase(String... cases) {
		this.cases.set(fromArray(cases));
	}

	@Override
	protected void updateBeforeSaying() {
		//nothing to do
	}

	@Override
	protected String getMainLine() {
		return cases.get().getRandom();
	}

	@Override
	public void edit(String before, String cases, String after) {
		super.edit(before, cases, after);
		if (cases != null) {
			setCases(cases);
		}
	}
	
	public void setCases(String cases) {
		this.cases.setValueString(cases);
	}
}
