package shtykh.parrots;


import org.apache.log4j.Logger;
import shtykh.parrots.onlyif.Booleaner;
import shtykh.parrots.when.Longer;
import shtykh.tweets.TwitterClient;
import shtykh.parrots.what.Stringer;

import java.util.Date;

/**
 * Created by shtykh on 29/03/15.
 */
public abstract class Parrot extends Thread {
	private static Logger log = Logger.getLogger(Parrot.class);
	private final Stringer what;
	private final Longer when;
	private final Booleaner ifWhat;
	private final TwitterClient tc;


	private Date next;
	private final String name;
	
	public Parrot(Stringer what,
				  Longer when,
				  Booleaner ifWhat,
				  TwitterClient tc, String name) {
		super();
		this.what = what;
		this.when = when;
		this.ifWhat = ifWhat;
		this.tc = tc;
		this.name = name;
		setDaemon(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (ifWhat.nextBoolean()) {
					tc.post(what.nextString());
				} else {
					log.info(name + ": Not tweeting");
				}
				long sleep = when.nextLong();
				next = new Date(System.currentTimeMillis() + sleep);
				log.info(name + ": Next attempt in " + next);
				Thread.sleep(sleep);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Date getNext() {
		return next;
	}

	public String getParrotName() {
		return name;
	}
}
