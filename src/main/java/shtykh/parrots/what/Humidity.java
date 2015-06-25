package shtykh.parrots.what;

import shtykh.parrots.poster.Poster;
import shtykh.tweets.Weather;
import shtykh.util.Util;

/**
 * Created by shtykh on 25/06/15.
 */
public class Humidity extends SomethingWithComments {
	private final Poster poster;
	private Weather currentWeather = null;

	public Humidity(Poster poster) {
		this.poster = poster;
		setCommentsBefore(
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
				setCommentsAfter(" А температура и вообще " + currentWeather.getTemp_c());
				break;
			case 1:
				setCommentsAfter(" А температура и вообще " + currentWeather.getTemp_f() + " (В фаренгейтах, конечно)");
				break;
			case 2:
				Weather perm = poster.getWeather("Perm");
				setCommentsAfter(" В Перми тем временем " + perm.getHumidity() + "%, #наминуточку");
				break;
			case 3:
				Weather moscow = poster.getWeather("Moscow");
				setCommentsAfter(" В Москве тем временем " + moscow.getHumidity() + "%, #наминуточку");
				break;
			default: // 4-(10-1)
				setCommentsAfter("");
				break;
				
		}
	}

	@Override
	protected String getMainLine() {
		return currentWeather.getHumidity() + "% #ВВвСК ";
	}
}
