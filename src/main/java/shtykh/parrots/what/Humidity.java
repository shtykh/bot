package shtykh.parrots.what;

import shtykh.parrots.poster.Poster;
import shtykh.tweets.Weather;
import shtykh.util.Util;

import static java.lang.Math.round;

/**
 * Created by shtykh on 25/06/15.
 */
public class Humidity extends SomethingWithComments {
	private final Poster poster;
	private Weather currentWeather = null;

	public Humidity(Poster poster) {
		this.poster = poster;
		setBefore(
				"Ах да, совсем забыла: ",
				"",
				"",
				"Кому интересно, ",
				"",
				"",
				""
		);
	}

	@Override
	protected void updateBeforeSaying() {
		currentWeather = poster.getWeather("State+college");
		switch(Util.random.nextInt(10)) {
			case 0: 
				setAfter(" А температура и вообще " + round(currentWeather.getTemp_c()));
				break;
			case 1:
				setAfter(" А температура и вообще " + round(currentWeather.getTemp_f()) + " (В фаренгейтах, конечно)");
				break;
			case 2:
				Weather perm = poster.getWeather("Perm");
				setAfter(" В Перми тем временем " + round(perm.getHumidity()) + "%, #наминуточку");
				break;
			case 3:
				Weather moscow = poster.getWeather("Moscow");
				setAfter(" В Москве тем временем " + round(moscow.getHumidity()) + "%, #наминуточку");
				break;
			default: // 4-(10-1)
				setAfter("");
				break;
				
		}
	}

	@Override
	protected String getMainLine() {
		return round(currentWeather.getHumidity()) + "% #ВВвСК ";
	}
}
