package shtykh.rest.parrot;

import shtykh.bot.booleaner.Probably;
import shtykh.bot.longer.OnceIn;
import shtykh.bot.stringer.Phrase;

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
	public FoodParrot() {
		super(food, new OnceIn(1, 3), new Probably(0.7), "FoodParrot");
	}
}
