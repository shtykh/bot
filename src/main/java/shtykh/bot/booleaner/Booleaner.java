package shtykh.bot.booleaner;

import org.json.JSONException;
import shtykh.tweets.TwitterAPIException;

import java.io.IOException;

/**
 * Created by shtykh on 29/03/15.
 */
public interface Booleaner {
	public boolean nextBoolean() throws TwitterAPIException, JSONException, IOException;
}
