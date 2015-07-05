package shtykh.parrots;

import shtykh.parrots.onlyif.Booleaner;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Stringer;
import shtykh.parrots.when.Longer;

/**
 * Created by shtykh on 05/07/15.
 */
public class CustomParrot extends Parrot {
	public CustomParrot(Stringer what, 
						Longer when, 
						Booleaner ifWhat, 
						Poster poster, 
						String name, 
						boolean forced) {
		super(what, when, ifWhat, poster, name, forced);
	}
}
