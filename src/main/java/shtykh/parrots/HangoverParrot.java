package shtykh.parrots;

import shtykh.parrots.onlyif.MoreLikelyOnWeekend;
import shtykh.parrots.when.OnceIn;
import shtykh.tweets.TwitterClient;

/**
 * Created by shtykh on 01/04/15.
 */
public class HangoverParrot extends Parrot {
	public HangoverParrot(TwitterClient tc) {
		super(() -> "#водички", new OnceIn(1, 2), new MoreLikelyOnWeekend(0.7, 0.2), tc, "HangoverParrot");
	}
}
