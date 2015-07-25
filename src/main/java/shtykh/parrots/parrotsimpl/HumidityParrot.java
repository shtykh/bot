package shtykh.parrots.parrotsimpl;

import shtykh.parrots.Parrot;
import shtykh.parrots.onlyif.Probably;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Humidity;
import shtykh.parrots.when.Daily;

import java.awt.*;

/**
 * Created by shtykh on 25/06/15.
 */
public class HumidityParrot extends Parrot {
	public HumidityParrot(Poster poster) {
		super(new Humidity(poster),
				new Daily(),
				new Probably(0.9),
				poster,
				"HumidityParrot", Color.blue.brighter().brighter().brighter());
	}
}
