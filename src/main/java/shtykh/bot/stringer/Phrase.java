package shtykh.bot.stringer;

import static shtykh.Util.random;

/**
 * Created by shtykh on 29/03/15.
 */
public class Phrase implements Stringer {
	private final String[] epithets;

	private String[] commentsAfter;
	private String[] commentsBefore;

	public Phrase(String... epithets) {
		this.epithets = epithets;
	}

	public void setCommentsAfter(String... commentsAfter) {
		this.commentsAfter = commentsAfter;
	}

	public void setCommentsBefore(String... commentsBefore) {
		this.commentsBefore = commentsBefore;
	}
	
	@Override
	public String nextString() {
		StringBuilder sb = new StringBuilder();
		if (commentsBefore != null && commentsBefore.length > 0) {
			String comment = randomFromArray(commentsBefore);
			sb.append(comment);
		}
		String epithet = randomFromArray(epithets);
		sb.append(epithet);
		if (commentsAfter != null && commentsAfter.length > 0) {
			String comment = randomFromArray(commentsAfter);
			sb.append(comment);
		}
		return sb.toString();
	}
	
	public String randomFromArray(String[] array) {
		return array[random.nextInt(array.length)];
	}
}
