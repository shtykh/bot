package shtykh.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.quedit.author.SinglePerson;
import shtykh.util.catalogue.MapCatalogue;
import shtykh.util.html.HtmlHelper;
import shtykh.util.html.TableBuilder;
import shtykh.util.html.form.build.ActionBuilder;
import shtykh.util.html.param.Parameter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static shtykh.util.html.HtmlHelper.href;
import static shtykh.util.html.HtmlHelper.htmlPage;
import static shtykh.util.html.form.param.FormParameterType.text;

/**
 * Created by shtykh on 01/10/15.
 */

@Component
@Path("/authors")
public class AuthorsCatalogue extends MapCatalogue<SinglePerson> {
	
	@Autowired
	private HtmlHelper htmlHelper;
	
	public AuthorsCatalogue() {
		super(SinglePerson.class, "authors");
		refresh();
	}

	@GET
	@Path("/list")
	public Response list() {
		refresh();
		String name = "Персонажи";
		TableBuilder table;
		URI uriList;
		URI uriNew;
		try {
			table = getPersonTable();
			uriList = htmlHelper.uriBuilder("/bot/rest/authors/list").build();
			uriNew = htmlHelper.uriBuilder("/bot/rest/authors/editform").build();
		} catch (URISyntaxException e) {
			return Response.status(500).entity(e.toString()).build();
		}
		String href = href(uriList, name);
		String body = folder.getAbsolutePath() + "<br>" + table.toString() + "<br>" + href(uriNew, "Добавить");
		return Response.status(Response.Status.OK).entity(htmlPage(name, href, body)).build();
	}

	private TableBuilder getPersonTable() throws URISyntaxException {
		TableBuilder table = new TableBuilder("Персонаж", "Редактировать", "Удалить");
		for (String name : getKeys()) {
			URI uriEdit = htmlHelper.uriBuilder("/bot/rest/authors/editform", new Parameter<>("name", name)).build();
			URI uriRemove = htmlHelper.uriBuilder("/bot/rest/authors/remove", new Parameter<>("name", name)).build();
			table.addRow(name, href(uriEdit, "Редактировать"), href(uriRemove, "Удалить"));
		}
		return table;
	}
	
	@GET
	@Path("/editform")
	public Response editForm(@QueryParam("name") String name) {
		SinglePerson person = get(name);
		if (person == null) {
			person = SinglePerson.mock();
		}
		return Response.status(Response.Status.OK).entity(htmlPage("Отредактируйте данные", editPersonAction.buildForm(person))).build();
	}

	@GET
	@Path("/remove")
	public Response removeMethod(@QueryParam("name") String name) {
		super.remove(name);
		return list();
	}

	@GET
	@Path("/edit")
	public Response edit(@QueryParam("firstName") String name, @QueryParam("lastName") String lastName, @QueryParam("city") String city) {
		SinglePerson p = new SinglePerson(name, lastName, city);
		add(p);
		return list();
	}

	private static ActionBuilder editPersonAction = new ActionBuilder("edit");
	static {
		try {
			editPersonAction.addParam(SinglePerson.class, "firstName", "Имя", text)
					.addParam(SinglePerson.class, "lastName", "Фамилия", text)
					.addParam(SinglePerson.class, "city", "Город", text);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		AuthorsCatalogue ac = new AuthorsCatalogue();
		ac.htmlHelper = new HtmlHelper();
		System.out.println(ac.list().getEntity());
		System.out.println("ok");
	}

	@Override
	protected String getFileName(SinglePerson p) {
		return p.toString();
	}
}
