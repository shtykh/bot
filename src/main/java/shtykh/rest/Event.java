package shtykh.rest;

import org.json.JSONException;
import shtykh.parrots.Parrot;
import shtykh.tweets.TwitterAPIException;
import shtykh.util.html.form.BooleanParameterMaterial;
import shtykh.util.html.form.CommentMaterial;
import shtykh.util.html.form.FormMaterial;
import shtykh.util.html.form.FormParameterMaterial;

import java.io.IOException;
import java.util.Date;

/**
 * Created by shtykh on 05/07/15.
 */
public class Event implements Comparable<Event>, FormMaterial {
	private static int lastId = 0;

	private FormParameterMaterial<Integer> id;
	private FormParameterMaterial<Parrot> parrot;
	private FormParameterMaterial<Boolean> isForced;
	private FormParameterMaterial<Date> time;

	public Event(Parrot parrot, Date time) {
		super();
		this.isForced = new BooleanParameterMaterial(false);
		this.parrot   = new CommentMaterial<>(parrot, Parrot.class);
		this.id       = new FormParameterMaterial<>(lastId++, Integer.class);
		this.time     = new FormParameterMaterial<>(time, Date.class);
	}

	@Override
	public String toString() {
		return parrot.getValueString() + id.getValueString();
	}

	public Parrot getParrot() {
		return parrot.get();
	}

	public Date getTime() {
		return time.get();
	}

	@Override
	public int compareTo(Event o) {
		return time.get().compareTo(o.time.get());
	}

	public int getId() {
		return id.get();
	}

	public boolean isInPast() {
		return time.get().before(new Date());
	}

	public boolean isForced() {
		return isForced.get();
	}

	public void setForced(boolean isForced) {
		this.isForced.set(isForced);
	}

	public void tryToSay() throws TwitterAPIException, JSONException, IOException {
		parrot.get().tryToSay(this);
	}

	public void setTime(String time) {
		this.time.setValueString(time);
	}
}
