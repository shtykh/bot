package shtykh.parrots;

import shtykh.util.html.HtmlHelper;
import shtykh.util.html.TableBuilder;

import java.util.Date;

/**
 * Created by shtykh on 25/06/15.
 */
public class PostEntry {
	private String post;
	private String response;
	private Date date;

	public PostEntry(String post, String response, Date date) {
		this.post = post;
		this.response = response;
		this.date = date;
	}

	public String getResponse() {
		return response;
	}

	public Date getDate() {
		return date;
	}
	
	public String getPost() {
		return post;
	}

	public String toHtml() {
		TableBuilder table = 
				new TableBuilder("Post", "Response", "Date")
						.addRow(post, response, date.toString());
		return HtmlHelper.htmlPage("Post", table.buildHtml());
	}
}
