package shtykh.parrots.parrotsimpl;

import shtykh.parrots.Parrot;
import shtykh.parrots.onlyif.MoreLikelyOnWeekend;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.when.OnceIn;

import java.awt.*;

/**
 * Created by shtykh on 01/04/15.
 */
public class HangoverParrot extends Parrot {
	public HangoverParrot(Poster poster) {
		super(() -> "#водички", new OnceIn(1, 2),
				new MoreLikelyOnWeekend(0.7, 0.2),
				poster,
				"HangoverParrot", Color.GREEN.brighter());
	}
}
