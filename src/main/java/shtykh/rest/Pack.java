package shtykh.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shtykh.quedit._4s.FormParameterMaterial4s;
import shtykh.quedit._4s.Meta4s;
import shtykh.quedit._4s._4Sable;
import shtykh.quedit.author.Authored;
import shtykh.quedit.author.MultiPerson;
import shtykh.quedit.author.Person;
import shtykh.quedit.numerator.QuestionNaturalNumerator;
import shtykh.quedit.numerator.QuestionNumerator;
import shtykh.quedit.question.Question;
import shtykh.util.StringSerializer;
import shtykh.util.catalogue.Catalogue;
import shtykh.util.catalogue.ListCatalogue;
import shtykh.util.html.ColoredTable;
import shtykh.util.html.HtmlHelper;
import shtykh.util.html.form.build.ActionBuilder;
import shtykh.util.html.form.material.FormMaterial;
import shtykh.util.html.param.Parameter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.Boolean.parseBoolean;
import static shtykh.util.Util.*;
import static shtykh.util.html.HtmlHelper.href;
import static shtykh.util.html.HtmlHelper.htmlPage;
import static shtykh.util.html.form.param.FormParameterType.*;

/**
 * Created by shtykh on 01/10/15.
 */
@Component
@Path("/pack")
public class Pack extends ListCatalogue<Question> implements FormMaterial, _4Sable, Authored {
	private static final Logger log = Logger.getLogger(Pack.class);
	
	private QuestionNumerator numerator;
	@Autowired
	private HtmlHelper htmlHelper;
	@Autowired
	private AuthorsCatalogue authors;
	
	private FormParameterMaterial4s metaInfo = new FormParameterMaterial4s(Meta4s.META, "");
	private FormParameterMaterial4s name = new FormParameterMaterial4s(Meta4s.TITLE, "Кубок РУДН");
	private FormParameterMaterial4s nameLJ = new FormParameterMaterial4s(Meta4s.TITLE_LJ, "Кубок РУДН");
	private FormParameterMaterial4s date = new FormParameterMaterial4s(Meta4s.DATE, "");
	private MultiPerson author;
	private FormParameterMaterial4s editor = new FormParameterMaterial4s(Meta4s.EDITOR, "Алексей Штых (Москва)");

	private static ActionBuilder editQuestionAction = new ActionBuilder("edit");
	private static ActionBuilder editAuthorAction = new ActionBuilder("editAuthor");
	private static ActionBuilder editPackAction = new ActionBuilder("editPack");
	private static ActionBuilder addEditorAction = new ActionBuilder("addEditor");

	static {
		try {
			editQuestionAction
					.addParam(Question.class, "number", "Номер", comment)
					.addParam(Question.class, "index", "Номер", hidden)
					.addParam(Question.class, "unaudible", "Примечания чтецу", textarea)
					.addParam(Question.class, "text", "Текст вопроса", textarea)
					.addParam(Question.class, "answer", "Ответ", textarea)
					.addParam(Question.class, "possibleAnswers", "Зачёт", textarea)
					.addParam(Question.class, "impossibleAnswers", "Незачёт", textarea)
					.addParam(Question.class, "comment", "Комментарий", textarea)
					.addParam(Question.class, "sources", "Источники (каждый с новой строки)", textarea)
					.addParam(Question.class, "color", "Цвет(для удобства редактора)", color)
			;
			editAuthorAction
					.addParam(Catalogue.class, "keys", "Добавить автора", select)
					.addParam(Question.class, "number", "Номер", comment)
					.addParam(Question.class, "index", "Номер", hidden)
			;
			addEditorAction
					.addParam(Catalogue.class, "keys", "Добавить редактора", select)
			;
			editPackAction
					.addParam(Pack.class, "name", "Название пакета", text)
					.addParam(Pack.class, "nameLJ", "Название пакета (для ЖЖ)", text)
					.addParam(Pack.class, "date", "Дата", text)
					.addParam(Pack.class, "editor", "Редакторы", comment)
					.addParam(Pack.class, "metaInfo", "Слово редактора", textarea)
			;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("/editPack")
	public Response editPack(
			@QueryParam("name") String name,
			@QueryParam("nameLJ") String nameLJ,
			@QueryParam("date") String date,
			@QueryParam("metaInfo") String metaInfo
	) {
		this.name.setValueString(name);
		this.nameLJ.setValueString(nameLJ);
		this.date.setValueString(date);
		this.metaInfo.setValueString(metaInfo);
		return home();
	}

	public Pack() {
		super(Question.class, "pack");
	}

	@GET
	@Path("/")
	public Response home() {
		refresh();
		ColoredTable questionsTable;
		URI uriList;
		URI uriNew;
		URI uriText;
		URI uriBuild;
		URI uriAuthors;
		String outFormat;
		try {
			questionsTable = getQuestionTable();
			uriList = htmlHelper.uriBuilder("/bot/rest/pack/").build();
			Parameter<String> parameter = new Parameter<>("index", String.valueOf(size()));
			uriNew = htmlHelper.uriBuilder("/bot/rest/pack/editForm", parameter).build();
			uriText = htmlHelper.uriBuilder("/bot/rest/pack/text").build();
			outFormat = readProperty("quedit.properties", "outFormat");
			uriBuild = htmlHelper.uriBuilder("/bot/rest/pack/build").addParameter("outFormat", outFormat).build();
			uriAuthors = htmlHelper.uriBuilder("/bot/rest/authors/list").build();

		} catch (URISyntaxException | FileNotFoundException e) {
			return Response.status(500).entity(e.toString()).build();
		}
		String href = href(uriList, name.get());
		String body =
				href(uriText, "Полный текст в 4s") + "<br>" +
				href(uriBuild, "Сгенерировать пакет") + "<br>" +
				folder.getAbsolutePath() + "<br>" +
				questionsTable.toString() + "<br>" +
				href(uriNew, "Добавить вопрос №" + numerator.getNumber(size())) + "<br><br>" +
				editPackAction.buildForm(this) + "<br>" +
				addEditorAction.buildForm(authors) + "<br>" +
				href(uriAuthors, "Каталог авторов") + "<br>" +
				"";
		return Response.status(Response.Status.OK).entity(htmlPage(name.get(), href, body)).build();
	}

	private ColoredTable getQuestionTable() throws URISyntaxException {
		ColoredTable table = new ColoredTable("Номер", "Ответ", "Редактировать", "Авторы", "В запас", "Вверх", "Вниз");
		for (int i = 0; i < size(); i++) {
			String index = String.valueOf(i);
			Parameter<String> parameter = new Parameter<>("index", index);
			URI home = htmlHelper.uriBuilder("/bot/rest/pack/").build();
			URI uriEdit = htmlHelper.uriBuilder("/bot/rest/pack/editForm", parameter).build();
			String questionColor = get(i).getColor();
			URI uriColor = htmlHelper.uriBuilder("/bot/rest/pack/nextColor", parameter, new Parameter<>("color", questionColor)).build();
			URI uriEditAuthor = htmlHelper.uriBuilder("/bot/rest/pack/editAuthorForm", parameter).build();
			URI uriUp = i == 0 ? home : htmlHelper.uriBuilder("/bot/rest/pack/up", parameter).build();
			URI uriDown = i == size() - 1 ? home : htmlHelper.uriBuilder("/bot/rest/pack/down", parameter).build();
			URI uriRemove = htmlHelper.uriBuilder("/bot/rest/pack/remove", parameter).build();
			URI uriReplace = htmlHelper.uriBuilder("/bot/rest/pack/replace", parameter).build();
			Person author = get(i).getAuthor();
			String authorString = "Добавить автора";
			if (author != null && StringUtils.isNotBlank(author.toString())) {
				authorString = author.toString();
			}
			table.addRow(href(uriColor, numerator.getNumber(i)), 
					get(i).getAnswer()
					,href(uriEdit, "Редактировать")
					,href(uriEditAuthor, authorString)
					,href(uriReplace, "В запас")
					,href(uriUp, "<--")
					,href(uriDown, "-->")
					);
			table.addColor(i + 1, 0, questionColor);
		}
		return table;
	}

	@Override
	protected void initFields() {
		super.initFields();
		numerator = new QuestionNaturalNumerator(1);
		author = new MultiPerson();
	}

	@GET
	@Path("/editForm")
	public Response editForm(@QueryParam("index") int index) {
		Question question = get(index);
		if (question == null) {
			question = Question.mock();
			question.setIndex(size());
		} else {
			question.setIndex(index);
		}
		question.setNumber(numerator.getNumber(question.getIndex()));
		String body = questionHtml(question) + editQuestionAction.buildForm(question);
		return Response
				.status(Response.Status.OK)
				.entity(htmlPage("Отредактируйте вопрос", body))
				.build();
	}
	private String questionHtml(Question question) {
		return question.toString().replace("\n", "<br>") + "<br>";
	}

	@GET
	@Path("/editAuthorForm")
	public Response editAuthorForm(@QueryParam("index") int index) throws URISyntaxException {
		Question question = get(index);
		if (question == null) {
			question = Question.mock();
			question.setIndex(size());
		} else {
			question.setIndex(index);
		}
		question.setNumber(numerator.getNumber(question.getIndex()));
		question.setAuthors(authors);
		String body = questionHtml(question) + "<br>"
				+ editAuthorAction.buildForm(question) 
				+ href(htmlHelper.uriBuilder("/bot/rest/authors/list").build(), "Каталог авторов");
		return Response
				.status(Response.Status.OK)
				.entity(htmlPage("Добавить автора", body))
				.build();
	}

	@GET
	@Path("/remove")
	public Response removeMethod(@QueryParam("index") int index) {
		super.remove(index);
		return home();
	}

	@GET
	@Path("/replace")
	public Response replace(@QueryParam("index") int index) {
		super.replace(index, "запас");
		return home();
	}

	@GET
	@Path("/up")
	public Response upMethod(@QueryParam("index") int index) {
		super.up(index);
		return home();
	}

	@GET
	@Path("/down")
	public Response downMethod(@QueryParam("index") int index) {
		super.down(index);
		return home();
	}

	@Override
	public void add(Integer number, Question item) {
		super.add(number, item);
		number(number, item);
	}

	@Override
	protected void swap(int key, int key2) {
		super.swap(key, key2);
		number(key, get(key2));
		number(key2, get(key));
	}

	private void number(Integer number, Question item) {
		item.setIndex(number);
		item.setNumber(numerator.getNumber(number));
	}

	@GET
	@Path("/edit")
	public Response edit(
			@QueryParam("index") int index,
			@QueryParam("unaudible") String unaudible,
			@QueryParam("color") String color,
			@QueryParam("text") String text,
			@QueryParam("answer") String answer,
			@QueryParam("possibleAnswers") String possibleAnswers,
			@QueryParam("impossibleAnswers") String impossibleAnswers,
			@QueryParam("comment") String comment,
			@QueryParam("sources") String sources
			) {
		Question question = get(index);
		if (question == null) {
			question = new Question();
		}
		question.setUnaudible(unaudible);
		question.setText(text);
		question.setAnswer(answer);
		question.setComment(comment);
		question.setImpossibleAnswers(impossibleAnswers);
		question.setPossibleAnswers(possibleAnswers);
		question.setSources(sources);
		question.setNumber(numerator.getNumber(index));
		question.setColor(color);
		add(index, question);
		return home();
	}

	@GET
	@Path("/editAuthor")
	public Response editAuthor(
			@QueryParam("index") int index,
			@QueryParam("keys") String author
			) {
		Question question = get(index);
		question.setAuthors(authors);
		question.addAuthor(author);
		add(index, question);
		return home();
	}

	@GET
	@Path("/addEditor")
	public Response addEditor(
			@QueryParam("keys") String author
	) {
		addAuthor(author);
		editor.setValueString(this.author.toString());
		return home();
	}

	public void addAuthor(String name) {
		if (author == null) {
			author = new MultiPerson();
		}
		if (authors == null) {
			throw new RuntimeException("Authors were null");
		}
		author.add(authors.get(name));
	}

	@GET
	@Path("/nextColor")
	public Response nextColor(
			@QueryParam("index") int index,
			@QueryParam("color") String colorHex
	) {
		StringSerializer<Color> serializer = StringSerializer.getForClass(Color.class);
		Question question = get(index);
		Color color = Color.decode(colorHex);
		if (color.equals(Color.white)) {
			color = Color.red;
		} else if (color.equals(Color.red)) {
			color = Color.yellow;
		} else if (color.equals(Color.yellow)) {
			color = Color.green;
		} else {
			color = Color.white;
		}
		colorHex = serializer.toString(color);
		question.setColor(colorHex);
		add(index, question);
		return home();
	}

	@GET
	@Path("/text")
	public Response text() throws IOException {
		String text4s = to4s();
		return Response.ok(htmlPage(name.get(), "", text4s.replace("\n", "<br>"))).build();
	}

	@GET
	@Path("/build")
	public Response build(@QueryParam("outFormat") String outFormat) throws IOException {
		boolean	debug = parseBoolean(readProperty("quedit.properties", "debug"));
		StringLogger logs = new StringLogger(log, debug);
		String text4s = to4s();
		logs.debug("text generated in 4s");
		String timestamp = timestamp("yyyyMMdd_HHmmss");
		File timestampFolder = new File(folder.getAbsolutePath() + "/" + timestamp);
		timestampFolder.mkdirs();
		logs.debug(timestampFolder + " is created");
		String name4sFile = timestampFolder.getAbsoluteFile() + "/4s.4s";
		write(new File(name4sFile), text4s);
		logs.debug("4s was written to " + name4sFile);
		File template = copyFileToDir(readProperty("quedit.properties", "templatedocx"), timestampFolder);
		logs.debug(template + " was created");
		String[] cmd = chgkComposeCmd(name4sFile, outFormat, "--nospoilers");
		if (call(logs, cmd) == 0) {
			logs.info("Файл формата " + outFormat + " успешно создан в папке " + timestampFolder);
		}
		template.delete();
		logs.debug(template + " was deleted");
		return Response.ok(htmlPage("Выгрузка", logs.toString().replace("\n", "<br>"))).build();
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.setValueString(name);
	}

	public static void main(String[] args) throws IOException {
		Pack pack = new Pack();
		pack.htmlHelper = new HtmlHelper();
		pack.authors = new AuthorsCatalogue();
		//System.out.println(pack.home().getEntity());
		System.out.println(pack.build(null).getEntity());
		//System.out.println(pack.editForm(0).getEntity());
		//System.out.println(pack.editAuthor(0, "Дмитрий Некрылов (Киев)"));
	}

	private static String[] chgkComposeCmd(String... parameters) throws FileNotFoundException {
		String[] cmd = new String[parameters.length + 2];
		cmd[0] = readProperty("quedit.properties", "python");
		cmd[1] = readProperty("quedit.properties", "chgk_composer");
		for (int i = 0; i < parameters.length; i++) {
			cmd[i + 2] = parameters[i];
		}
		return cmd;
	}

	@Override
	public String to4s() {
		numerator.renumber(getList());
		StringBuilder sb = new StringBuilder();
		append(sb, name);
		append(sb, nameLJ);
		append(sb, date);
		append(sb, editor);
		append(sb, metaInfo);
		for (Question question : getList()) {
			sb.append(question.to4s() + "\n");
		}
		return sb.toString();
	}

	@Override
	public Person getAuthor() {
		return author;
	}
	@Override
	public void setAuthor(MultiPerson author) {
		this.author = author;
	}
}
