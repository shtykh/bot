package shtykh.rest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.parrots.Event;
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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
	private List<Event> events;

	@Autowired
	private Poster poster;
	private HtmlHelper htmlHelper;
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private long timeout = 60000;

	public void init() throws HeadlessException, IOException, JSONException, TwitterAPIException {
		htmlHelper = new HtmlHelper(HOST, PORT);
		parrots = new ArrayList<>();
		events = new ArrayList<>();
		parrots.add(new FoodParrot(poster, false));
		parrots.add(new HangoverParrot(poster, false));
		parrots.add(new LocationParrot(poster, new LocationIsChanged("Москва"), false));
		parrots.add(new SweetsParrot(poster, new Sweets(), false));
		parrots.add(new HumidityParrot(poster, true));

		log.info("Starting parrots");
		for (Parrot parrot : parrots) {
			events.add(parrot.generateEvent());
		}
		log.info("Starting ticker");
		Thread launcher = new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					sayPast();
				} catch (InterruptedException | IOException | JSONException | TwitterAPIException e) {
					throw new RuntimeException(e);
				}
			}
		};
		launcher.setDaemon(true);
		launcher.start();
	}

	private void sayPast() throws TwitterAPIException, JSONException, IOException {
		List<Event> pastEvents = getPast(events);
		for (Event pastEvent : pastEvents) {
			pastEvent.getParrot().tryToSay();
			events.add(pastEvent.getParrot().generateEvent());
		}
		events.removeAll(pastEvents);

	}

	private List<Event> getPast(List<Event> events) {
		List<Event> past = new ArrayList<>();
		Collections.sort(events);
		Date now = new Date();
		for (Event event : events) {
			if (event.getTime().before(now)) {
				past.add(event);
			} else {
				break;
			}
		}
		return past;
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
	@Path("/events")
	public Response events(@QueryParam("number") int number) {
		if (number >= parrots.size()) {
			return Response.status(404).entity("Wrong parrot number:" + number + "\n" +
					"It should be " + (parrots.size() - 1) + " or less").build();
		} else {
			Parrot parrot = parrots.get(number);
			TableBuilder table = new TableBuilder("Id", "Parrot", "When", "Remove");
			Collections.sort(events);
			for (Event event : events) {
				if (event.getParrot().getParrotName().equals(parrot.getParrotName())) {
					table.addRow(getEventTableRow(event));
				}
			}
			String result = htmlPage(parrot.getParrotName() + " events", table.buildHtml());
			return Response.status(200).entity(result).build();
		}
	}

	private String[] getEventTableRow(Event event) {
		String id = String.valueOf(event.getId());
		return new String[]{
				id,
				event.getParrot().getParrotName(),
				event.getTime().toString(),
				getPath("removeEvent", "Remove" , new Parameter("id", id))
		};
	}

	@GET
	@Path("/allEvents")
	public Response allEvents() {
		TableBuilder table = allEventsTable();
		String result = htmlPage("All events", table.buildHtml());
		return Response.status(200).entity(result).build();
	}

	private TableBuilder allEventsTable() {
		TableBuilder table = new TableBuilder("Id", "Parrot", "When", "Remove");
		Collections.sort(events);
		for (Event event : events) {
			table.addRow(getEventTableRow(event));
		}
		return table;
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
	@Path("/removeEvent")
	public Response removeEvent(@QueryParam("id") int id) {
		Event eventToRemove = null;
		for (Event event : events) {
			if (event.getId() == id) {
				eventToRemove = event;
				break;
			}
		}
		if (eventToRemove == null) {
			return Response.status(404).entity("Event with id: " + id + " not found!").build();
		} else {
			events.remove(eventToRemove);
			return home();
		}
	}

	@GET
	@Path("/addEvent")
	public Response addEvent(@QueryParam("number") int number) {
		if (number >= parrots.size()) {
			return Response.status(404).entity("Wrong parrot number:" + number + "\n" +
					"It should be " + (parrots.size() - 1) + " or less").build();
		} else {
			Parrot parrot = parrots.get(number);
			Event event = parrot.generateEvent();
			events.add(event);
			return home();
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/")
	public Response home() {
		TableBuilder table = new TableBuilder(
				"Parrot Name",
				"Events",
				"Last Posts",
				"Posts",
				"Forced to post", 
				"Force to post",
				"Post right now",
				"Add an addEvent");
		for (int i = 0; i < parrots.size(); i++) {
			Parrot parrot = parrots.get(i);
			Parameter numberI = new Parameter("number", String.valueOf(i));
			table.addRow(
					parrot.getParrotName(), 
					getPath("events", "Events", 
							numberI),
					getPath("log", "Last posts", 
							numberI, 
							new Parameter("posts", String.valueOf(15))),
					String.valueOf(parrot.getPostsNumber()), 
					String.valueOf(parrot.getForceAttempt()), 
					getPath("force", "Force " + parrot.getParrotName(), 
							numberI, 
							new Parameter("force", String.valueOf(true))),
					getPath("say", "Say " + parrot.getParrotName(), 
							numberI),
					getPath("addEvent", "New event " + parrot.getParrotName(),
							numberI)
					);
		}
		String body = table.buildHtml() + "<br/>" + allEventsTable().buildHtml();
		if(parrots.isEmpty()) {
			body = "is empty";
		}
		String page = htmlPage("Parrots list", getPath("", "Parrots are:"), body);
		return Response.status(200).entity(page).build();
	}
	
	private String getPath(String method, String name, Parameter... parameters) {
		try {
			URIBuilder uriBuilder = htmlHelper.uriBuilder("/bot/rest/parrots/" + method, parameters);
			return HtmlHelper.href(uriBuilder.build(), name);
		} catch (URISyntaxException e) {
			return HtmlHelper.href(htmlHelper.getHome(), "error");
		}
	}
	
}
