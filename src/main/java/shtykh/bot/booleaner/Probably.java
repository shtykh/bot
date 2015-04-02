package shtykh.bot.booleaner;

import static shtykh.Util.random;

/**
 * Created by shtykh on 01/04/15.
 */
public class Probably implements Booleaner {
	private double probability;

	public Probably(double probability) {
		this.probability = probability;
	}

	@Override
	public boolean nextBoolean() {
		return random.nextDouble() < this.probability;
	}
}
