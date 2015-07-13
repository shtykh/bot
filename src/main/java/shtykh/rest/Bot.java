package shtykh.rest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.parrots.Parrot;
import shtykh.parrots.onlyif.LocationIsChanged;
import shtykh.parrots.onlyif.Randomly;
import shtykh.parrots.parrotsimpl.*;
import shtykh.parrots.poster.Poster;
import shtykh.parrots.what.Phrase;
import shtykh.parrots.what.SomethingWithComments;
import shtykh.parrots.what.Sweets;
import shtykh.parrots.when.Daily;
import shtykh.tweets.TwitterAPIException;
import shtykh.ui.UiUtil;
import shtykh.util.html.HtmlHelper;
import shtykh.util.html.TableBuilder;
import shtykh.util.html.form.ActionBuilder;
import shtykh.util.html.form.FormMaterial;
import shtykh.util.html.param.FormParameter;
import shtykh.util.html.param.FormParameterSignature;
import shtykh.util.html.param.Parameter;

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

import static shtykh.util.html.HtmlHelper.href;
import static shtykh.util.html.HtmlHelper.htmlPage;
import static shtykh.util.html.TagBuilder.tag;
import static shtykh.util.html.param.FormParameterType.*;

/**
 * Created by shtykh on 29/03/15.
 */
@Component
@Path("/parrots")
public class Bot extends JFrame implements FormMaterial {
	private static final Logger log = Logger.getLogger(Bot.class);
	
	private Map<String, Parrot> parrots;
	private List<Event> events;

	@Autowired
	private Poster poster;
	private HtmlHelper htmlHelper;
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private FormParameter<Long> timeout;
	private long nextShot = 0;
	public void init() throws HeadlessException, IOException, JSONException, TwitterAPIException {
		htmlHelper = new HtmlHelper(HOST, PORT);
		ArrayList<Parrot> parrotsList = new ArrayList<>();
		parrots = new HashMap<>();
		events = new ArrayList<>();
		timeout = new FormParameter<>("timeout", 60000L, Long.class, number);
		
		parrotsList.add(new FoodParrot(poster));
		parrotsList.add(new HangoverParrot(poster));
		parrotsList.add(new LocationParrot(poster, new LocationIsChanged("Москва")));
		parrotsList.add(new SweetsParrot(poster, new Sweets()));
		parrotsList.add(new HumidityParrot(poster));

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
					while (true) {
						nextShot = System.currentTimeMillis() + timeout.getValue();
						Thread.sleep(timeout.getValue());
						sayPast();
					}
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
			pastEvent.tryToSay();
			events.add(pastEvent.getParrot().generateEvent());
		}
		events.removeAll(pastEvents);
	}

	private List<Event> getPast(List<Event> events) {
		List<Event> past = new ArrayList<>();
		Collections.sort(events);
		Date now = new Date();
		for (Event event : events) {
			if (event.isInPast()) {
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
			Bot bot = new Bot();
			bot.init();
			bot.addParrot("Test");
			System.out.println(bot.home().getEntity());
			System.out.println(bot.editParrotForm("Test").getEntity());
			System.exit(0);
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
			TableBuilder table = createEventTableBuilder();
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

	@GET
	@Path("/allEvents")
	public Response allEvents() {
		TableBuilder table = allEventsTable();
		String result = htmlPage("All events", table.buildHtml());
		return Response.status(200).entity(result).build();
	}

	private TableBuilder allEventsTable() {
		TableBuilder table = createEventTableBuilder();
		Collections.sort(events);
		for (Event event : events) {
			table.addRow(getEventTableRow(event));
		}
		return table;
	}

	private TableBuilder createEventTableBuilder() {
		return new TableBuilder(
				/*0*/"Id",
				/*1*/"Parrot",
				/*2*/"When",
				/*3*/"Edit",
				/*4*/"Force",
				/*5*/"Remove");
	}

	private String[] getEventTableRow(Event event) {
		String id = String.valueOf(event.getId());
		String pastColor = event.isInPast() ? "red" : "green";
		boolean forced = event.isForced();
		return new String[]{
				/*0*/id,
				/*1*/event.getParrot().getParrotName(),
				/*2*/colorTag(event.getTime().toString(), pastColor),
				/*3*/getPath("editEventForm", "Edit" , new Parameter("id", id)),
				/*4*/getPath("forceEvent",
						forced ? colorTag("Unforce", "red") : colorTag("Force", "green"),
						new Parameter("id", id),
						new Parameter("force", String.valueOf(!forced))),
				/*5*/getPath("removeEvent", "Remove" , new Parameter("id", id))
		};
	}

	private String colorTag(String value, String color) {
		return "<font color=\"" + color +"\">" + value + "</color";
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
	@Path("/forceEvent")
	public Response forceEvent(@QueryParam("id") int id, @QueryParam("force") boolean force) {
		Event eventToForce = null;
		for (Event event : events) {
			if (event.getId() == id) {
				eventToForce = event;
				break;
			}
		}
		if (eventToForce == null) {
			return Response.status(404).entity("Event with id: " + id + " not found!").build();
		} else {
			eventToForce.setForced(force);
			return home();
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
	@Path("/editEvent")
	public Response editEvent(@QueryParam("id") int id, 
							  	@QueryParam("time") String time,
								@QueryParam("force") boolean force) {
		Event eventToEdit = null;
		for (Event event : events) {
			if (event.getId() == id) {
				eventToEdit = event;
				break;
			}
		}
		if (eventToEdit == null) {
			return Response.status(404).entity("Event with id: " + id + " not found!").build();
		} else {
			eventToEdit.setTime(time);
			eventToEdit.setForced(force);
			return home();
		}
	}

	private static ActionBuilder editEventAction;

	static {
		try {
			editEventAction = new ActionBuilder("editEvent")
					.addParam(Event.class.getDeclaredField("isForced"), new FormParameterSignature("force", checkbox))
					.addParam(Event.class.getDeclaredField("id"), new FormParameterSignature("id", hidden))
					.addParam(Event.class.getDeclaredField("time"), new FormParameterSignature("time", datetime_local));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	@GET
	@Path("/editEventForm")
	public Response editEventForm(@QueryParam("id") int id) {
		Event eventToEdit = null;
		for (Event event : events) {
			if (event.getId() == id) {
				eventToEdit = event;
				break;
			}
		}
		if (eventToEdit == null) {
			return Response.status(404).entity("Event with id: " + id + " not found!").build();
		} else {
			String body = editEventAction.buildForm(eventToEdit);
			return Response.status(200).entity(htmlPage("Edit event", body)).build();
		}
	}

	private static ActionBuilder setTimeoutAction;
	static {
		try {
			setTimeoutAction = new ActionBuilder("setTimeOut")
					.addParam(Bot.class.getDeclaredField("timeout"), new FormParameterSignature("timeout", number));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/setTimeout")
	public Response setTimeout(@QueryParam("timeout") String timeout) {
		this.timeout.setValue(timeout);
		return home();
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
	@Path("/editParrotForm")
	public Response editParrotForm(@QueryParam("name") String name) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
			return Response.status(200).entity(editParrotAction.buildForm(parrot)).build();
		}
	}

	private static ActionBuilder editParrotAction;
	static {
		try {
			editParrotAction = new ActionBuilder("editParrot")
					.addParam(Parrot.class.getDeclaredField("name"), new FormParameterSignature("name", hidden))
					.addParam(SomethingWithComments.class.getDeclaredField("before"), new FormParameterSignature("before", text))
					.addParam(Phrase.class.getDeclaredField("cases"), new FormParameterSignature("cases", text))
					.addParam(SomethingWithComments.class.getDeclaredField("after"), new FormParameterSignature("after", text));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("/editParrot")
	public Response editParrot(@QueryParam("name") String name,
							   @QueryParam("before") String before,
							   @QueryParam("cases") String cases,
							   @QueryParam("after") String after) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
			if (parrot.getWhat() instanceof SomethingWithComments) {
				((SomethingWithComments) parrot.getWhat()).edit(before, cases, after);
			}
			return home();
		}
	}

	@GET
	@Path("/addParrot")
	public Response addParrot(@QueryParam("name") String name) {
		Parrot newParrot = new CustomParrot(
				new Phrase("I am " + name), 
				new Daily(), 
				new Randomly(), 
				poster, 
				name);
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
				"Edit",
				"Events",
				"Last Posts",
				"Posts",
				"Post right now",
				"Add an event",
				"Remove the parrot");
		for (Parrot parrot: parrots.values()) {
			addParrotToTable(table, parrot);
		}
		String body =
				setTimeoutAction.buildForm(this) +
				timeInfo() +
				getPath("addParrot", 
						"Add custom Parrot", 
						new Parameter<>("name", "CustomParrot" + parrots.size())) +
				"<br/>" +
				table.buildHtml() + 
				"<br/>" + 
				allEventsTable().buildHtml();
		if(parrots.isEmpty()) {
			body = "is empty";
		}
		String page = htmlPage("Parrots list", "Parrots are:", body);
		return Response.status(200).entity(page).build();
	}

	private void addParrotToTable(TableBuilder table, Parrot parrot) {
		String name = parrot.getParrotName();
		Parameter nameParameter = new Parameter<>("name", name);
		table.addRow(
				parrot.getParrotName(),
				getPath("editParrotForm", "Edit", 
						nameParameter),
				getPath("events", "Events",
						nameParameter),
				getPath("log", "Last posts",
						nameParameter,
						new Parameter<>("posts", String.valueOf(15))),
				String.valueOf(parrot.getPostsNumber()),
				getPath("say", "Say",
						nameParameter),
				getPath("addEvent", "New event",
						nameParameter),
				getPath("removeParrot", "Remove",
						nameParameter)
		);
	}

	private String timeInfo() {
		String now = "Now  is " + new Date();
		String br = "<br>";
		String next = "Next is " + new Date(nextShot);
		String refreshButton = getPath("", "" + (nextShot - System.currentTimeMillis()) / 1000) + " sec";
		String timeTillNext = tag("h2").build("Time till next shot: " + refreshButton);
		String result = now + br + next + timeTillNext;
		return result;
	}

	private String getPath(String method, String name, Parameter... parameters) {
		try {
			URIBuilder uriBuilder = htmlHelper.uriBuilder("/bot/rest/parrots/" + method, parameters);
			return href(uriBuilder.build(), name);
		} catch (URISyntaxException e) {
			return href(htmlHelper.getHome(), "error");
		}
	}
}
