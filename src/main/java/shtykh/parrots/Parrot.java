package shtykh.parrots;


import org.apache.log4j.Logger;
import shtykh.parrots.onlyif.Booleaner;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Stringer;
import shtykh.parrots.when.Longer;
import shtykh.tweets.TwitterAPIException;

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

	private LinkedList<String> postsLog;
	private Date next;
	private final String name;
	
	public Parrot(Stringer what,
				  Longer when,
				  Booleaner ifWhat,
				  Poster poster, String name) {
		this.what = what;
		this.when = when;
		this.ifWhat = ifWhat;
		this.poster = poster;
		this.name = name;
		postsLog = new LinkedList<>();
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
				if (ifWhat.nextBoolean()) {
					say();
				} else {
					log.info(name + ": Not tweeting");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void say() throws TwitterAPIException {
		String post = poster.post(what.nextString());
		postsLog.push(post);
	}

	public Date getNext() {
		return next;
	}

	public String getParrotName() {
		return name;
	}
	
	public String getPostLog(int n) {
		int postsNumber = Math.min(n, postsLog.size());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < postsNumber; i++) {
			sb.append(postsLog.get(i));
		}
		return sb.toString();
	}
}
