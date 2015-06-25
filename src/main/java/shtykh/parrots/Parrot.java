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
public abstract class Parrot extends Thread {
	private static Logger log = Logger.getLogger(Parrot.class);
	private final Stringer what;
	private final Longer when;
	private final Booleaner ifWhat;
	private final Poster poster;

	private LinkedList<PostEntry> postsLog;
	private Date next;

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
		setDaemon(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				long sleep = when.nextLong();
				next = new Date(System.currentTimeMillis() + sleep);
				log.info(name + ": Next attempt in " + next);
				Thread.sleep(sleep);
				if (forceAttempt) {
					say();
					forceAttempt = false;
				} else {
					if (ifWhat.nextBoolean()) {
						say();
					} else {
						pushToLog("Not tweeting");
						log.info(name + ": Not tweeting");
					}
				}
			} catch (InterruptedException | IOException | JSONException | TwitterAPIException e) {
				pushToLog(e.getMessage());
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

	public Date getNext() {
		return next;
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
