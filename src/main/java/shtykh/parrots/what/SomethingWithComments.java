package shtykh.parrots.what;

import static shtykh.util.Util.random;

/**
 * Created by shtykh on 25/06/15.
 */
public abstract class SomethingWithComments implements Stringer {
	private String[] commentsAfter;
	private String[] commentsBefore;

	public SomethingWithComments() {}

	public void setCommentsAfter(String... commentsAfter) {
		this.commentsAfter = commentsAfter;
	}

	public void setCommentsBefore(String... commentsBefore) {
		this.commentsBefore = commentsBefore;
	}

	@Override
	public String nextString() {
		updateBeforeSaying();
		StringBuilder sb = new StringBuilder();
		if (commentsBefore != null && commentsBefore.length > 0) {
			String comment = randomFromArray(commentsBefore);
			sb.append(comment);
		}
		sb.append(getMainLine());
		if (commentsAfter != null && commentsAfter.length > 0) {
			String comment = randomFromArray(commentsAfter);
			sb.append(comment);
		}
		return sb.toString();
	}

	protected abstract void updateBeforeSaying();
	protected abstract String getMainLine();

	public String randomFromArray(String[] array) {
		return array[random.nextInt(array.length)];
	}
}
