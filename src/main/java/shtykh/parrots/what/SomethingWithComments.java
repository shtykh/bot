package shtykh.parrots.what;

import shtykh.util.html.form.FormMaterial;
import shtykh.util.html.param.FormParameter;

import static shtykh.parrots.what.CSV.fromArray;

/**
 * Created by shtykh on 25/06/15.
 */
public abstract class SomethingWithComments implements Stringer, FormMaterial {
	private final FormParameter<CSV> before
			= new FormParameter<>("before", new CSV(), CSV.class);
	private final FormParameter<CSV> after
			= new FormParameter<>("after", new CSV(), CSV.class);

	public SomethingWithComments() {}

	public void setAfter(String... after) {
		this.after.setValue(fromArray(after));
	}

	public void setBefore(String... before) {
		this.before.setValue(fromArray(before));
	}

	@Override
	public String nextString() {
		updateBeforeSaying();
		StringBuilder sb = new StringBuilder();
		if (! before.getValue().isEmpty()) {
			String comment = before.getValue().getRandom();
			sb.append(comment);
		}
		sb.append(getMainLine());
		if (! after.getValue().isEmpty()) {
			String comment = after.getValue().getRandom();
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
