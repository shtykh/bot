package shtykh.parrots.onlyif;

import static shtykh.Util.random;

/**
 * Created by shtykh on 01/04/15.
 */
public class Randomly implements Booleaner {
	@Override
	public boolean nextBoolean() {
		return random.nextBoolean();
	}
}
