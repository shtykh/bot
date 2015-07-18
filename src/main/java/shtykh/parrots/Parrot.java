package shtykh.parrots;


import org.apache.log4j.Logger;
import org.json.JSONException;
import shtykh.parrots.onlyif.Booleaner;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Stringer;
import shtykh.parrots.when.Longer;
import shtykh.rest.Event;
import shtykh.tweets.TwitterAPIException;
import shtykh.util.html.HtmlHelper;
import shtykh.util.html.TableBuilder;
import shtykh.util.html.form.FormMaterial;
import shtykh.util.html.form.FormParameterMaterial;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by shtykh on 29/03/15.
 */

public abstract class Parrot implements FormMaterial  {
	private static Logger log = Logger.getLogger(Parrot.class);
	private final Stringer what;
	private final Longer when;
	private final Booleaner ifWhat;
	private final Poster poster;

	private LinkedList<PostEntry> postsLog;

	private final FormParameterMaterial<String> name;
	
	public Parrot(Stringer what,
				  Longer when,
				  Booleaner ifWhat,
				  Poster poster, 
				  String name) {
		this.what = what;
		this.when = when;
		this.ifWhat = ifWhat;
		this.poster = poster;
		this.name = new FormParameterMaterial<>(name, String.class);
		postsLog = new LinkedList<>();
	}
	
	public Event generateEvent() {
		long sleep = when.nextLong();
		Date next = new Date(System.currentTimeMillis() + sleep);
		return new Event(this, next);
	}
	
	public String tryToSay(Event event) throws TwitterAPIException, JSONException, IOException {
		if (event.isForced() && ifWhat.nextBoolean()) {
			return say();
		} else {
			String message = "Not happened to say";
			pushToLog(message);
			log.info(name + ": " + message);
			return message;
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
		return name.getValueString();
	}

	public void setParrotName(String newName) {
		name.set(newName);
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

	@Override
	public String toString() {
		return getParrotName();
	}

	public Stringer getWhat() {
		return what;
	}
}
