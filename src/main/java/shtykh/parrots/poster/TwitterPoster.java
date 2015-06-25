package shtykh.parrots.poster;

import org.apache.log4j.Logger;
import org.json.JSONException;
import shtykh.tweets.Location;
import shtykh.tweets.TwitterAPIException;
import shtykh.tweets.TwitterClient;
import shtykh.tweets.Weather;

import java.io.IOException;

/**
 * Created by shtykh on 02/04/15.
 */
public class TwitterPoster implements Poster {
	private static Logger log = Logger.getLogger(TwitterPoster.class);
	private TwitterClient twitterClient;
	
	public TwitterPoster() throws IOException, JSONException {
		twitterClient = new TwitterClient(null);
	}

	@Override
	public String post(String msg) {
		try {
			return twitterClient.post(msg);
		} catch (TwitterAPIException e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
	}

	@Override
	public Location getLocation() {
		try {
			return twitterClient.getLocation();
		} catch (JSONException | TwitterAPIException | IOException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	@Override
	public Weather getWeather(String query) {
		try {
			return twitterClient.getWeather(query);
		} catch (JSONException | IOException | TwitterAPIException e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
