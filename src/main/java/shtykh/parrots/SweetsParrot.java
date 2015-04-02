package shtykh.parrots;

import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Sweets;
import shtykh.parrots.when.Daily;

/**
 * Created by shtykh on 01/04/15.
 */
public class SweetsParrot extends Parrot {
	public SweetsParrot(Poster poster, Sweets sweets) {
		super(sweets, new Daily(), sweets, poster, "SweetsParrot");
	}
}
