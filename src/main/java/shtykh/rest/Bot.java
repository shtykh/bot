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
import shtykh.util.html.ColoredTable;
import shtykh.util.html.HtmlHelper;
import shtykh.util.html.TableBuilder;
import shtykh.util.html.form.material.FormMaterial;
import shtykh.util.html.form.material.FormParameterMaterial;
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
	private FormParameterMaterial<Long> timeout;
	private long nextShot = 0;
	public void init() throws HeadlessException, IOException, JSONException, TwitterAPIException {
		htmlHelper = new HtmlHelper(HOST, PORT);
		ArrayList<Parrot> parrotsList = new ArrayList<>();
		parrots = new HashMap<>();
		events = new ArrayList<>();
		timeout = new FormParameterMaterial<>(60000L, Long.class);
		
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
						nextShot = System.currentTimeMillis() + timeout.get();
						Thread.sleep(timeout.get());
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
			ColoredTable table = createEventTableBuilder();
			Collections.sort(events);
			int eventNumber = 1;
			for (Event event : events) {
				if (event.getParrot().getParrotName().equals(parrot.getParrotName())) {
					table.addRow(getEventTableRow(event));
					table.addColor(eventNumber++, 1, event.getParrot().getColorHex());
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
		ColoredTable table = createEventTableBuilder();
		Collections.sort(events);
		int eventNumber = 1;
		for (Event event : events) {
			table.addRow(getEventTableRow(event));
			table.addColor(eventNumber++, 1, event.getParrot().getColorHex());
		}
		return table.setTitle("Events:");
	}

	private ColoredTable createEventTableBuilder() {
		return new ColoredTable(
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
		Parameter idParam = new Parameter<>("id", id);
		return new String[]{
				/*0*/id,
				/*1*/event.getParrot().getParrotName(),
				/*2*/colorTag(event.getTime().toString(), pastColor),
				/*3*/getPath("editEventForm", "Edit" , idParam),
				/*4*/getPath("forceEvent",
						forced ? colorTag("Unforce", "red") : colorTag("Force", "green"),
						idParam,
						new Parameter<>("force", String.valueOf(!forced))),
				/*5*/getPath("removeEvent", "Remove" , idParam)
		};
	}

	private String colorTag(String value, String color) {
		return tag("font").params(new Parameter<>("color", color)).build(value);
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
			String body = Actions.editEventAction.buildForm(eventToEdit);
			return Response.status(200).entity(htmlPage("Edit event", body)).build();
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/setTimeout")
	public Response setTimeout(@QueryParam("timeout") String timeout) {
		this.timeout.setValueString(timeout);
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
			return Response.status(200).entity(
					htmlPage("Edit Parrot " + name, Actions.editParrotAction.buildForm(parrot))).build();
		}
	}

	@GET
	@Path("/editParrot")
	public Response editParrot(@QueryParam("name") String name,
							   @QueryParam("rename") String rename,
							   @QueryParam("before") String before,
							   @QueryParam("cases") String cases,
							   @QueryParam("after") String after,
							   @QueryParam("color") String color) {
		if (!parrots.containsKey(name)) {
			return Response.status(404).entity("Parrot:" + name + " not found").build();
		} else {
			Parrot parrot = parrots.get(name);
			if (parrot.getWhat() instanceof SomethingWithComments) {
				((SomethingWithComments) parrot.getWhat()).edit(before, cases, after);
			}
			if (color != null) {
				parrot.setColor(color);
			}
			if (rename != null && !rename.equals(name)) {
				parrots.remove(name);
				parrot.setParrotName(rename);
				parrots.put(rename, parrot);
			}
			return home();
		}
	}

	@GET
	@Path("/addParrot")
	public Response addParrot(@QueryParam("name") String name) {
		Parrot newParrot = new CustomParrot(
				new Phrase("I am " + name, "I am " + name, "I am " + name, "I am " + name, "I am fucking " + name),
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
		ColoredTable table = new ColoredTable(
				"Parrot Name",
				"Edit",
				"Events",
				"Last Posts",
				"Posts",
				"Post right now",
				"Add an event",
				"Remove the parrot");
		int parrotNumber = 1;
		for (Parrot parrot: parrots.values()) {
			addParrotToTable(table, parrot, parrotNumber++);
		}
		String body =
				allEventsTable().buildHtml() + 
				"<br/>" +
				table.setTitle("Parrots:").buildHtml() +
				getPath("addParrot",
						"Add custom Parrot",
						new Parameter<>("name", "CustomParrot" + parrots.size())) + 
				"<br/><br/>" +
				Actions.setTimeoutAction.buildForm(this);
		if(parrots.isEmpty()) {
			body = "is empty";
		}
		String page = htmlPage("Parrots list", timeInfo(), body);
		return Response.status(200).entity(page).build();
	}

	private void addParrotToTable(ColoredTable table, Parrot parrot, int parrotNumber) {
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
		table.addColor(parrotNumber, 0, parrot.getColorHex());
	}

	private String timeInfo() {
		String refreshButton = getPath("", "" + (nextShot - System.currentTimeMillis()) / 1000) + " sec";
		return tag("h2").build("Time till next shot: " + refreshButton);
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
