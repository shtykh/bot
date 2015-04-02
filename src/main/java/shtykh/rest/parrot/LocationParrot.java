package shtykh.rest.parrot;

import shtykh.bot.booleaner.LocationIsChanged;
import shtykh.bot.longer.OnceIn;

/**
 * Created by shtykh on 01/04/15.
 */
public class LocationParrot extends Parrot {
	public LocationParrot(LocationIsChanged locationIsChanged) {
		super(() -> getCityName(locationIsChanged) + ", я в тебе", 
				new OnceIn(2, 3), 
				locationIsChanged,
				"LocationParrot");
		locationIsChanged.setPoster(poster);
	}

	public static String getCityName(LocationIsChanged locationIsChanged) {
		return locationIsChanged.getCityName();
	}
}
