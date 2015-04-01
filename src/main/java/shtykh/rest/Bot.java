package shtykh.rest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import shtykh.parrots.*;
import shtykh.parrots.onlyif.LocationIsChanged;
import shtykh.parrots.what.Sweets;
import shtykh.tweets.TwitterAPIException;
import shtykh.tweets.TwitterClient;
import shtykh.ui.UiUtil;

import javax.swing.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
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

	public Bot() throws HeadlessException, IOException, JSONException {
		log.info("Starting parrots");
		TwitterClient twitterClient = new TwitterClient(this);
		initParrots(twitterClient);
		for (Parrot parrot : parrots) {
			parrot.start();
		}
	}

	private void initParrots(TwitterClient twitterClient) {
		parrots = new ArrayList<>();
		
		parrots.add(new FoodParrot(twitterClient));
		parrots.add(new HangoverParrot(twitterClient));
		parrots.add(new LocationParrot(twitterClient, new LocationIsChanged("Москва")));
		parrots.add(new SweetsParrot(twitterClient, new Sweets()));
	}

	public static void main(String[] args)
			throws TwitterAPIException, JSONException, IOException, InterruptedException {
		try {
			new Bot();
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
}
