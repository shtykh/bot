package shtykh.parrots.poster;

import shtykh.tweets.Location;

/**
 * Created by shtykh on 02/04/15.
 */
public interface Poster {
	String post(String msg);
	Location getLocation();
}
