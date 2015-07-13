package shtykh.rest;

import org.json.JSONException;
import shtykh.parrots.Parrot;
import shtykh.tweets.TwitterAPIException;
import shtykh.util.html.form.FormMaterial;
import shtykh.util.html.param.BooleanParameter;
import shtykh.util.html.param.Comment;
import shtykh.util.html.param.FormParameter;

import java.io.IOException;
import java.util.Date;

import static shtykh.util.html.param.FormParameterType.*;

/**
 * Created by shtykh on 05/07/15.
 */
public class Event implements Comparable<Event>, FormMaterial {
	private static int lastId = 0;

	private FormParameter<Integer> id;
	private Comment<Parrot> parrot;
	private FormParameter<Boolean> isForced;
	private FormParameter<Date> time;

	public Event(Parrot parrot, Date time) {
		super();
		this.isForced = new BooleanParameter("force", false);
		this.parrot   = new Comment<>("Parrot", parrot);
		this.id       = new FormParameter<>("id", lastId++, Integer.class, number);
		this.time     = new FormParameter<>("Time", time, Date.class, datetime_local);
	}

	@Override
	public String toString() {
		return parrot.getValue() + id.getValueString();
	}

	public Parrot getParrot() {
		return parrot.getValue();
	}

	public Date getTime() {
		return time.getValue();
	}

	@Override
	public int compareTo(Event o) {
		return time.getValue().compareTo(o.time.getValue());
	}

	public int getId() {
		return id.getValue();
	}

	public boolean isInPast() {
		return time.getValue().before(new Date());
	}

	public boolean isForced() {
		return isForced.getValue();
	}

	public void setForced(boolean isForced) {
		this.isForced.setValue(isForced);
	}

	public void tryToSay() throws TwitterAPIException, JSONException, IOException {
		parrot.getValue().tryToSay(this);
	}

	public void setTime(String time) {
		this.time.setValue(time);
	}
}
