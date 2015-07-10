package shtykh.rest;

import org.json.JSONException;
import shtykh.parrots.Parrot;
import shtykh.parrots.onlyif.Randomly;
import shtykh.parrots.parrotsimpl.CustomParrot;
import shtykh.parrots.poster.TwitterPoster;
import shtykh.parrots.what.SomethingWithComments;
import shtykh.parrots.when.Daily;
import shtykh.tweets.TwitterAPIException;
import shtykh.util.HtmlHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shtykh on 05/07/15.
 */
public class Event implements Comparable<Event> {
	private static int lastId = 0;
	private int id;
	private Parrot parrot;
	private Date time;
	private boolean isForced;
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");

	public Event(Parrot parrot, Date time) {
		super();
		id = lastId ++;
		this.parrot = parrot;
		this.time = time;
	}

	@Override
	public String toString() {
		return parrot.getParrotName() + " " + time;
	}

	public Parrot getParrot() {
		return parrot;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public int compareTo(Event o) {
		return time.compareTo(o.time);
	}

	public int getId() {
		return id;
	}

	public void setTime(String time) {
		try {
			this.time = df.parse(time);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String getTimeAsString() {
		return df.format(time);
	}

	public boolean isInPast() {
		return time.before(new Date());
	}

	public boolean isForced() {
		return isForced;
	}

	public void setForced(boolean isForced) {
		this.isForced = isForced;
	}

	public void tryToSay() throws TwitterAPIException, JSONException, IOException {
		parrot.tryToSay(this);
	}
	
	public HtmlHelper.FormBuilder editForm() {
		return new HtmlHelper.FormBuilder()
						.addComment("Parrot = " + parrot.getParrotName())
						.addComment("Is forced = " + isForced)
						.addParameter("id", id)
						.addParameter("time", getTimeAsString());
	}

	public static void main(String[] args) throws IOException, JSONException {
		System.out.println(new Event(new CustomParrot(
					new SomethingWithComments() {
						@Override
						protected void updateBeforeSaying() {
			
						}
			
						@Override
						protected String getMainLine() {
							return null;
						}
					}, 
					new Daily(), 
					new Randomly(), 
					new TwitterPoster(), 
					"Parrot0"), 
				new Date()).editForm()
				.setAction("action")
				.build());
	}
}
