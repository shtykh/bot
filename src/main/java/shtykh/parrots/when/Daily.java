package shtykh.parrots.when;

import org.apache.log4j.Logger;

/**
 * Created by shtykh on 29/03/15.
 */
public class Daily implements Longer {
	private static Logger log = Logger.getLogger(Daily.class);
	
	@Override
	public long nextLong() {
		return (long) (1440 * 60 * 1000);
	}
}
