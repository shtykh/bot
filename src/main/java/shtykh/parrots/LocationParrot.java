package shtykh.parrots;

import shtykh.parrots.onlyif.LocationIsChanged;
import shtykh.parrots.when.OnceIn;
import shtykh.tweets.TwitterClient;

/**
 * Created by shtykh on 01/04/15.
 */
public class LocationParrot extends Parrot {
	public LocationParrot(TwitterClient tc, LocationIsChanged locationIsChanged) {
		super(() -> getCityName(locationIsChanged) + ", я в тебе", new OnceIn(2, 3), locationIsChanged, tc, "LocationParrot");
		locationIsChanged.setTwitterClient(tc);
	}

	public static String getCityName(LocationIsChanged locationIsChanged) {
		return locationIsChanged.getCityName();
	}
}
