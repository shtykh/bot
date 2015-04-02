package shtykh.parrots.onlyif;

import org.json.JSONException;
import shtykh.parrots.poster.Poster;
import shtykh.tweets.TwitterAPIException;

import java.io.IOException;

/**
 * Created by shtykh on 01/04/15.
 */
public class LocationIsChanged implements Booleaner {
	private String cityName;
	private Poster poster;

	public LocationIsChanged() {
		this("");
	}

	public LocationIsChanged(String initialCityName) {
		this.cityName = initialCityName;
	}

	@Override
	public boolean nextBoolean() throws TwitterAPIException, JSONException, IOException {
		String newCityName = poster.getLocation().getName();
		boolean changed = !cityName.equals(newCityName);
		cityName = newCityName;
		return changed;
	}

	public String getCityName() {
		return cityName;
	}

	public void setPoster(Poster poster) {
		this.poster = poster;
	}
	
}
