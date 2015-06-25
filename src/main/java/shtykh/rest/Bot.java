package shtykh.rest;

import org.apache.http.client.utils.URIBuilder;
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
import shtykh.util.HtmlHelper;
import shtykh.util.Parameter;
import shtykh.util.TableBuilder;

import javax.swing.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static shtykh.util.HtmlHelper.htmlPage;

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
	private HtmlHelper htmlHelper;
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;

	public void init() throws HeadlessException, IOException, JSONException {
		htmlHelper = new HtmlHelper(HOST, PORT);
		parrots = new ArrayList<>();
		parrots.add(new FoodParrot(poster, false));
		parrots.add(new HangoverParrot(poster, false));
		parrots.add(new LocationParrot(poster, new LocationIsChanged("Москва"), false));
		parrots.add(new SweetsParrot(poster, new Sweets(), false));
		parrots.add(new HumidityParrot(poster, true));

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
	@Path("/force")
	public Response force(@QueryParam("number") int number, @QueryParam("force") boolean force) {
		if (number >= parrots.size()) {
			return Response.status(404).entity("Wrong parrot number:" + number + "\n" +
					"It should be " + (parrots.size() - 1) + " or less").build();
		} else {
			Parrot parrot = parrots.get(number);
			parrot.setForceAttempt(force);
			String result = parrot.getParrotName() + ": is forced to post at the next attempt: \n" + force;
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Path("/say")
	public Response say(@QueryParam("number") int number) {
		if (number >= parrots.size()) {
			return Response.status(404).entity("Wrong parrot number:" + number + "\n" +
					"It should be " + (parrots.size() - 1) + " or less").build();
		} else {
			Parrot parrot = parrots.get(number);
			String words = parrot.say();
			String result = parrot.getParrotName() + ": said: \n" + words;
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/list")
	public Response showList() {
		TableBuilder table = new TableBuilder(
				"Parrot Name",
				"Next attempt time",
				"Last Posts",
				"Posts",
				"Forced to post", 
				"Force to post",
				"Post right now");
		for (int i = 0; i < parrots.size(); i++) {
			Parrot parrot = parrots.get(i);
			Parameter numberI = new Parameter("number", String.valueOf(i));
			table.addRow(
					parrot.getParrotName(), 
					parrot.getNext().toString(), 
					getPath("log", "Last posts", 
							numberI, 
							new Parameter("posts", String.valueOf(15))),
					String.valueOf(parrot.getPostsNumber()), 
					String.valueOf(parrot.getForceAttempt()), 
					getPath("force", "Force " + parrot.getParrotName(), 
							numberI, 
							new Parameter("force", String.valueOf(true))),
					getPath("say", "Say " + parrot.getParrotName(), 
							numberI)
					);
		}
		String body = table.buildHtml();
		if(parrots.isEmpty()) {
			body = "is empty";
		}
		String page = htmlPage("Parrots list", "Parrots are:", body);
		return Response.status(200).entity(page).build();
	}
	
	private String getPath(String method, String name, Parameter... parameters) {
		try {
			URIBuilder uriBuilder = htmlHelper.uriBuilder("/bot/rest/parrots/" + method, parameters);
			return htmlHelper.href(uriBuilder.build(), name);
		} catch (URISyntaxException e) {
			return htmlHelper.href(htmlHelper.getHome(), "error");
		}
	}
	
}
