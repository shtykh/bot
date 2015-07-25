package shtykh.parrots.what;

import shtykh.util.html.form.material.FormMaterial;
import shtykh.util.html.form.material.FormParameterMaterial;

import static shtykh.parrots.what.CSV.fromArray;

/**
 * Created by shtykh on 25/06/15.
 */
public abstract class SomethingWithComments implements Stringer, FormMaterial {
	private final FormParameterMaterial<CSV> before
			= new FormParameterMaterial<>(new CSV(), CSV.class);
	private final FormParameterMaterial<CSV> after
			= new FormParameterMaterial<>(new CSV(), CSV.class);

	public SomethingWithComments() {}

	public void setAfter(String... after) {
		this.after.set(fromArray(after));
	}

	public void setBefore(String... before) {
		this.before.set(fromArray(before));
	}

	@Override
	public String nextString() {
		updateBeforeSaying();
		StringBuilder sb = new StringBuilder();
		if (! before.get().isEmpty()) {
			String comment = before.get().getRandom();
			sb.append(comment);
		}
		sb.append(getMainLine());
		if (! after.get().isEmpty()) {
			String comment = after.get().getRandom();
			sb.append(comment);
		}
		return sb.toString();
	}

	protected abstract void updateBeforeSaying();
	protected abstract String getMainLine();

	public void edit(String before, String cases, String after) {
		if (before != null) {
			setBefore(before);
		}
		if (after != null) {
			setAfter(after);
		}
	}
}
