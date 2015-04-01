package shtykh.parrots;

import shtykh.parrots.what.Sweets;
import shtykh.parrots.when.Daily;
import shtykh.tweets.TwitterClient;

/**
 * Created by shtykh on 01/04/15.
 */
public class SweetsParrot extends Parrot {
	public SweetsParrot(TwitterClient tc, Sweets sweets) {
		super(sweets, new Daily(), sweets, tc, "SweetsParrot");
	}
}
