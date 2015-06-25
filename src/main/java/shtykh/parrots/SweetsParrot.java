package shtykh.parrots;

import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Sweets;
import shtykh.parrots.when.Daily;

/**
 * Created by shtykh on 01/04/15.
 */
public class SweetsParrot extends Parrot {
	private final Sweets sweets;

	public SweetsParrot(Poster poster, Sweets sweets, boolean forced) {
		super(sweets, new Daily(), sweets, poster, "SweetsParrot", forced);
		this.sweets = sweets;
	}

	@Override
	public String say() {
		sweets.add(30);
		return super.say();
	}
}
