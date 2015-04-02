package shtykh.rest.parrot;

import shtykh.bot.longer.Daily;
import shtykh.bot.stringer.Sweets;

/**
 * Created by shtykh on 01/04/15.
 */
public class SweetsParrot extends Parrot {
	public SweetsParrot(Sweets sweets) {
		super(sweets, new Daily(), sweets, "SweetsParrot");
	}
}
