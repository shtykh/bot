package shtykh.quedit._4s;

/**
 * Created by shtykh on 02/10/15.
 */
public enum Meta4s {
	TITLE("###"),
	TITLE_LJ("###LJ"),
	EDITOR("#EDITOR"),
	DATE("#DATE"),
	META("#"),
	QUESTION("?"),
	NUMBER("№"),
	ANSWER("!"),
	EQUAL_ANSWER("="),
	NOT_EQUAL_ANSWER("!="),
	COMMENT("/"),
	SOURCES("^"),
	AUTHORS("@");
	private final String symbol;


	Meta4s(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
}
