package shtykh.rest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.parrots.*;
import shtykh.parrots.onlyif.LocationIsChanged;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Sweets;
import shtykh.tweets.TwitterAPIException;
import shtykh.ui.UiUtil;

import javax.swing.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shtykh on 29/03/15.
 */
@Component
@Path("/parrots")
public class Bot extends JFrame {
	private static final Logger log = Logger.getLogger(Bot.class);
	private List<Parrot> parrots;
	
	@Autowired
	private Poster poster;
	
	private static final String HOST = "http://localhost"; 
	private static final String PORT = "8080";

	public void init() throws HeadlessException, IOException, JSONException {
		parrots = new ArrayList<>();
		parrots.add(new FoodParrot(poster));
		parrots.add(new HangoverParrot(poster));
		parrots.add(new LocationParrot(poster, new LocationIsChanged("Москва")));
		parrots.add(new SweetsParrot(poster, new Sweets()));

		log.info("Starting parrots");
		for (Parrot parrot : parrots) {
			parrot.start();
		}
	}

	public static void main(String[] args)
			throws TwitterAPIException, JSONException, IOException, InterruptedException {
		try {
			new Bot().init();
		} catch (IOException | JSONException e) {
			log.error(e);
			UiUtil.showError("Ошибка при инициализации бота", e, null);
		}
	}

	@GET
	@Path("/when")
	public Response when(@QueryParam("number") int number) {
		if (number >= parrots.size()) {
			return Response.status(404).entity("Wrong parrot number:" + number + "\n" +
					"It should be " + (parrots.size() - 1) + " or less").build();
		} else {
			Parrot parrot = parrots.get(number);
			String result = parrot.getParrotName() + ": next attempt will be " + parrot.getNext();
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Path("/log")
	public Response log(@QueryParam("number") int number, @QueryParam("posts") int posts) {
		if (number >= parrots.size()) {
			return Response.status(404).entity("Wrong parrot number:" + number + "\n" +
					"It should be " + (parrots.size() - 1) + " or less").build();
		} else {
			Parrot parrot = parrots.get(number);
			String result = parrot.getParrotName() + ": posts: \n" + parrot.getPostLog(posts);
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/list")
	public Response showList() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table border=1>");
		for (int i = 0; i < parrots.size(); i++) {
			Parrot parrot = parrots.get(i);
			sb.append("<tr>");
			sb.append("<td>" + parrot.getParrotName() + "</td>" +
					  "<td>" + parrot.getNext() + "</td>" +
					  "<td>" + getPath("log?number=" + i + "&posts=" + 15, "Last posts") + "</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		String body = sb.toString();
		if(parrots.isEmpty()) {
			body = "is empty";
		}
		String page = htmlPage("Parrots list", "Parrots are:", body);
		return Response.status(200).entity(page).build();
	}
	
	private String htmlPage(String title, String header, String body) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>" +
				title +
				"</title><body><h1>" +
				header +
				"</h1>");
		sb.append(body);
		sb.append("</body></html>");
		return sb.toString();
	}
	
	private String getPath(String suffix, String name) {
		String href = HOST + ":" + PORT + "/bot/rest/parrots/" + suffix;
		return "<a href="+ href + ">" + name + "</a>";
	}
	
}
