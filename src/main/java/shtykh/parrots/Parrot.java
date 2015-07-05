package shtykh.parrots;


import org.apache.log4j.Logger;
import org.json.JSONException;
import shtykh.parrots.onlyif.Booleaner;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Stringer;
import shtykh.parrots.when.Longer;
import shtykh.tweets.TwitterAPIException;
import shtykh.util.HtmlHelper;
import shtykh.util.TableBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by shtykh on 29/03/15.
 */
public class Parrot {
	private static Logger log = Logger.getLogger(Parrot.class);
	private final Stringer what;
	private final Longer when;
	private final Booleaner ifWhat;
	private final Poster poster;

	private LinkedList<PostEntry> postsLog;

	private final String name;
	
	private boolean forceAttempt = true;
	
	public Parrot(Stringer what,
				  Longer when,
				  Booleaner ifWhat,
				  Poster poster, 
				  String name, boolean forced) {
		this.what = what;
		this.when = when;
		this.ifWhat = ifWhat;
		this.poster = poster;
		this.name = name;
		postsLog = new LinkedList<>();
		this.forceAttempt = forced;
	}
	
	public Event generateEvent() {
		long sleep = when.nextLong();
		Date next = new Date(System.currentTimeMillis() + sleep);
		return new Event(this, next);
	}
	
	public String tryToSay() throws TwitterAPIException, JSONException, IOException {
		if (forceAttempt) {
			forceAttempt = false;
			return say();
		} else {
			if (ifWhat.nextBoolean()) {
				return say();
			} else {
				String message = "Not happened to say";
				pushToLog(message);
				log.info(name + ": " + message);
				return message;
			}
		}
	}

	public String say() {
		String post = what.nextString();
		String result = poster.post(post);
		PostEntry postEntry = pushToLog(post, result);
		return postEntry.toHtml();
	}

	private PostEntry pushToLog(String post) {
		return pushToLog(post, "");
	}

	private PostEntry pushToLog(String post, String responce) {
		PostEntry postEntry = new PostEntry(post, responce, new Date());
		postsLog.push(postEntry);	
		return postEntry;
	}

	public String getParrotName() {
		return name;
	}
	
	public String getPostLog(int n) {
		TableBuilder table = new TableBuilder("Post", "Response", "Date");
		int postsNumber = Math.min(n, postsLog.size());
		for (int i = 0; i < postsNumber; i++) {
			PostEntry postEntry = postsLog.get(i);
			table.addRow(
					postEntry.getPost(), 
					postEntry.getResponse(), 
					postEntry.getDate().toString());
		}
		return HtmlHelper.htmlPage(name + " last " + postsNumber + " posts", table.buildHtml());
	}

	public int getPostsNumber() {
		return postsLog.size();
	}

	public void setForceAttempt(boolean force) {
		forceAttempt = force;
	}

	public boolean getForceAttempt() {
		return forceAttempt;
	}
}
