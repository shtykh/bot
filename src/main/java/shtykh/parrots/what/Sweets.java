package shtykh.parrots.what;

import org.apache.log4j.Logger;
import shtykh.parrots.onlyif.Booleaner;

import static shtykh.util.Util.random;

/**
 * Created by shtykh on 29/03/15.
 */
public class Sweets implements Stringer, Booleaner {
	private static final Logger log = Logger.getLogger(Sweets.class);
	private boolean newParty = true;
	private int number;
	private final Phrase black;
	private final Phrase white;

	public Sweets() {
		this.number = 0;
		this.newParty = true;
		black = new Phrase("шоколадная. ", "коричневая. ");
		black.setAfter("Но день будет всё равно хорошим :)",
				":(",
				"Не зря у меня болела голова!");
		white = new Phrase("ванильная. ", "белая. ");
		white.setAfter("И погода хорошая с утра :)",
				":)",
				"Так я и знала :)",
				"Всем доброго утра!");
	}

	public void add(int delta) {
		newParty = true;
		number += delta;
	}

	@Override
	public String nextString() {
		StringBuilder sb = new StringBuilder();
		if(newParty) {
			sb.append("Партия конфет #птичка_певунья подоспела :) ");
			newParty = false;
		}
		number--;
		if(number == 0) {
			sb.append("Последняя из этой партии #птичка_певунья - ");
		} else {
			if (random.nextBoolean()) {
				sb.append("Сегодня ");
			}
			sb.append("#птичка_певунья - ");
		}
		if (random.nextBoolean()) {
			sb.append(white.nextString());
		} else {
			sb.append(black.nextString());
		}
		return sb.toString();
	}

	public boolean isEmpty() {
		return number == 0;
	}

	@Override
	public boolean nextBoolean() {
		if (isEmpty()) {
			if (random.nextDouble() < 0.1) {
				int numberOfSweets = random.nextInt(20) + 20;
				add(numberOfSweets);
				log.info("New candy party has arrived: " + numberOfSweets + " candies :)");
				return true;
			} else {
				log.info("New candy party hasn't arrived :(");
				return false;
			}
		} else {
			return true;
		}
	}
}
