package shtykh.parrots.poster;

import shtykh.tweets.Location;
import shtykh.tweets.Weather;

/**
 * Created by shtykh on 02/04/15.
 */
public interface Poster {
	String post(String msg);
	Location getLocation();
	Weather getWeather(String query);
}
