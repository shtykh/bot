package shtykh.bot.longer;

import org.apache.log4j.Logger;

import static shtykh.Util.random;

/**
 * Created by shtykh on 29/03/15.
 */
public class OnceIn implements Longer {
	private static Logger log = Logger.getLogger(OnceIn.class);

	private final int daysMinimum;
	private final int daysMaximum;

	public OnceIn(int daysMinimum, int daysMaximum) {
		this.daysMinimum = daysMinimum;
		this.daysMaximum = daysMaximum;
	}

	@Override
	public long nextLong() {
		int minutesInDay = 1440;
		int minutes = random.nextInt((daysMaximum - daysMinimum) * minutesInDay) + daysMinimum * minutesInDay;
		return ((long) minutes) * 60 * 1000;
	}
}
