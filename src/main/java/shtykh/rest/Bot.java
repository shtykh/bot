package shtykh.rest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.parrots.*;
import shtykh.parrots.Event;
import shtykh.parrots.onlyif.LocationIsChanged;
import shtykh.parrots.onlyif.Randomly;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.SomethingWithComments;
import shtykh.parrots.what.Sweets;
import shtykh.parrots.when.Daily;
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
import java.util.*;
import java.util.List;

import static shtykh.util.HtmlHelper.htmlPage;

/**
 * Created by shtykh on 29/03/15.
 */
@Component
@Path("/parrots")
public class Bot extends JFrame {
	private static final Logger log = Logger.getLogger(Bot.class);
	
	private Map<String, Parrot> parrots;
	private List<Event> events;

	@Autowired
	private Poster poster;
	private HtmlHelper htmlHelper;
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private long timeout = 60000;

	public void init() throws HeadlessException, IOException, JSONException, TwitterAPIException {
		htmlHelper = new HtmlHelper(HOST, PORT);
		ArrayList<Parrot> parrotsList = new ArrayList<>();
		parrots = new HashMap<>();
		events = new ArrayList<>();
		parrotsList.add(new FoodParrot(poster, false));
		parrotsList.add(new HangoverParrot(poster, false));
		parrotsList.add(new LocationParrot(poster, new LocationIsChanged("Москва"), false));
		parrotsList.add(new SweetsParrot(poster, new Sweets(), false));
		parrotsList.add(new HumidityParrot(poster, true));

		log.info("Starting parrots");
		for (Parrot parrot : parrotsList) {
			parrots.put(parrot.getParrotName(), parrot);
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
	public Response events(@QueryParam("name") String name) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
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
	public Response log(@QueryParam("name") String name, @QueryParam("posts") int posts) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
			String result = parrot.getParrotName() + ": posts: \n" + parrot.getPostLog(posts);
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Path("/force")
	public Response force(@QueryParam("name") String name, @QueryParam("force") boolean force) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
			parrot.setForceAttempt(force);
			String result = parrot.getParrotName() + ": is forced to post at the next attempt: \n" + force;
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Path("/say")
	public Response say(@QueryParam("name") String name) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
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
	public Response addEvent(@QueryParam("name") String name) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
			Event event = parrot.generateEvent();
			events.add(event);
			return home();
		}
	}

	@GET
	@Path("/addParrot")
	public Response addParrot(@QueryParam("name") String name) {
		Parrot newParrot = new CustomParrot(new SomethingWithComments() {
			@Override
			protected void updateBeforeSaying() {
				
			}

			@Override
			protected String getMainLine() {
				return null;
			}
		}, new Daily(), new Randomly(), poster, name, false);
		parrots.put(name, newParrot);
		return home();
	}

	@GET
	@Path("/removeParrot")
	public Response removeParrot(@QueryParam("name") String name) {
		parrots.remove(name);
		return home();
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
				"Add an event",
				"Remove the parrot");
		for (Parrot parrot: parrots.values()) {
			String name = parrot.getParrotName();
			Parameter nameParameter = new Parameter("name", name);
			table.addRow(
					parrot.getParrotName(), 
					getPath("events", "Events", 
							nameParameter),
					getPath("log", "Last posts", 
							nameParameter,
							new Parameter("posts", String.valueOf(15))),
					String.valueOf(parrot.getPostsNumber()), 
					String.valueOf(parrot.getForceAttempt()), 
					getPath("force", "Force", 
							nameParameter,
							new Parameter("force", String.valueOf(true))),
					getPath("say", "Say", 
							nameParameter),
					getPath("addEvent", "New event",
							nameParameter),
					getPath("removeParrot", "Remove",
							nameParameter)
					);
		}
		String body = getPath("addParrot", "Add custom Parrot", new Parameter("name", "CustomParrot" + parrots.size())) +
				table.buildHtml() + 
				"<br/>" + 
				allEventsTable().buildHtml();
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
