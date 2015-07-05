package shtykh.parrots;

import java.util.Date;

/**
 * Created by shtykh on 05/07/15.
 */
public class Event implements Comparable<Event> {
	private static int lastId = 0;
	private int id;
	private Parrot parrot;
	private Date time;

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
}
