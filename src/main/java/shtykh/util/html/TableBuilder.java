package shtykh.util.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static shtykh.util.html.TagBuilder.tag;

/**
 * Created by shtykh on 06/04/15.
 */
public class TableBuilder {
	private static final int BORDER = 1;
	private List<String[]> contents;

	private String title = null;

	public TableBuilder(String... hat) {
		contents = new ArrayList<>();
		if (hat.length != 0) {
			addRow(hat);
		}
	}

	public TableBuilder addRow(String... strings) {
		contents.add(strings);
		return this;
	}

	public TableBuilder addRows(Collection<String[]> rows) {
		contents.addAll(rows);
		return this;
	}

	public TableBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public String buildHtml() {
		StringBuilder sb = new StringBuilder();
		if (title != null);
		sb.append(tag("h2").build(title));
		sb.append("<table border=" + BORDER + ">");
		for (String[] row : contents) {
			sb.append("<tr>");
			for (String cell : row) {
				sb.append("<td>");
				sb.append(cell);
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
}
