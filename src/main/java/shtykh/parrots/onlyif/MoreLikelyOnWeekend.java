package shtykh.parrots.onlyif;

import java.util.Calendar;

import static shtykh.Util.random;

/**
 * Created by shtykh on 01/04/15.
 */
public class MoreLikelyOnWeekend implements Booleaner {
	private final double weekendProbability;
	private final double workdayProbability;

	public MoreLikelyOnWeekend(double weekendProbability, double workdayProbability) {
		this.weekendProbability = weekendProbability;
		this.workdayProbability = workdayProbability;
	}

	@Override
	public boolean nextBoolean() {
		int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		double probability = 0;
		switch (i) {
			//суббота-воскресенье
			case 1:
			case 7:
				probability = weekendProbability;
				break;
			default:
				probability = workdayProbability;
		}
		return random.nextDouble() < probability;
	}
}
