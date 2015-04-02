package shtykh.rest.parrot;

import shtykh.bot.booleaner.MoreLikelyOnWeekend;
import shtykh.bot.longer.OnceIn;

/**
 * Created by shtykh on 01/04/15.
 */
public class HangoverParrot extends Parrot {
	public HangoverParrot() {
		super(	() -> "#водички", new OnceIn(1, 2),
				new MoreLikelyOnWeekend(0.7, 0.2), 
				"HangoverParrot");
	}
}
