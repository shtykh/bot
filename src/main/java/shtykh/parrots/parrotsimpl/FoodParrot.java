package shtykh.parrots.parrotsimpl;

import shtykh.parrots.Parrot;
import shtykh.parrots.onlyif.Probably;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Phrase;
import shtykh.parrots.when.OnceIn;

import java.awt.*;

/**
 * Created by shtykh on 29/03/15.
 */
public class FoodParrot extends Parrot {
	private static Phrase food = new Phrase("мармеладками",
											"курицей",
											"едой",
											"мясом",
											"свёклой",
											"ватой",
											"мятой",
											"колбасой",
											"колбаской",
											"сгущёнкой"
											);
	static {
		food.setBefore("Обожралась ",
				"Обкушалась ",
				"Ухомячилась ",
				"Объелась ",
				"Ужралась ",
				"Уелась ");
		food.setAfter(
				". Тошнит",
				". Умираю",
				". Плохо мне :(",
				" #ОфигевшийОпоссумОленька",
				"");
	}
	public FoodParrot(Poster poster) {
		super(food, new OnceIn(1, 3), new Probably(0.7), poster, "FoodParrot", Color.YELLOW.brighter());
	}
}
