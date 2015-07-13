package shtykh.parrots.what;
/**
 * Created by shtykh on 25/06/15.
 */
public abstract class SomethingWithComments implements Stringer {
	private final CommaSeparatedValues commentsAfter = new CommaSeparatedValues();
	private final CommaSeparatedValues commentsBefore = new CommaSeparatedValues();

	public SomethingWithComments() {}

	public void setCommentsAfter(String... commentsAfter) {
		this.commentsAfter
			.fromArray(commentsAfter);
	}

	public void setCommentsBefore(String... commentsBefore) {
		this.commentsBefore
			.fromArray(commentsBefore);
	}

	@Override
	public String nextString() {
		updateBeforeSaying();
		StringBuilder sb = new StringBuilder();
		if (! commentsBefore.isEmpty()) {
			String comment = commentsBefore.getRandom();
			sb.append(comment);
		}
		sb.append(getMainLine());
		if (! commentsAfter.isEmpty()) {
			String comment = commentsBefore.getRandom();
			sb.append(comment);
		}
		return sb.toString();
	}

	protected abstract void updateBeforeSaying();
	protected abstract String getMainLine();
}
