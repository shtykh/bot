package shtykh.parrots;

import shtykh.parrots.onlyif.LocationIsChanged;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.when.OnceIn;

/**
 * Created by shtykh on 01/04/15.
 */
public class LocationParrot extends Parrot {
	public LocationParrot(Poster poster, LocationIsChanged locationIsChanged) {
		super(() -> getCityName(locationIsChanged) + ", я в тебе", 
				new OnceIn(2, 3), 
				locationIsChanged, 
				poster, 
				"LocationParrot");
		locationIsChanged.setPoster(poster);
	}

	public static String getCityName(LocationIsChanged locationIsChanged) {
		return locationIsChanged.getCityName();
	}
}
