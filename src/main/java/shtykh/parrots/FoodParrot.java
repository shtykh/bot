package shtykh.parrots;

import shtykh.parrots.onlyif.Probably;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Phrase;
import shtykh.parrots.when.OnceIn;

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
											"колбасой");
	static {
		food.setCommentsBefore("Обожралась ",
				"Ухомячилась ",
				"Объелась ",
				"Ужралась ",
				"Уелась ");
		food.setCommentsAfter(
				". Тошнит",
				". Плохо мне :(",
				" #ОфигевшийОпоссумОленька",
				"");
	}
	public FoodParrot(Poster poster) {
		super(food, new OnceIn(1, 3), new Probably(0.7), poster, "FoodParrot");
	}
}
