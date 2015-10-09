package shtykh.quedit.pack;

import com.sun.jersey.core.header.FormDataContentDisposition;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import shtykh.quedit._4s.Parser4s;
import shtykh.quedit._4s._4Sable;
import shtykh.quedit.author.Authored;
import shtykh.quedit.author.MultiPerson;
import shtykh.quedit.author.Person;
import shtykh.quedit.numerator.QuestionNaturalNumerator;
import shtykh.quedit.numerator.QuestionNumerator;
import shtykh.quedit.question.Question;
import shtykh.rest.AuthorsCatalogue;
import shtykh.util.Jsonable;
import shtykh.util.StringSerializer;
import shtykh.util.Util;
import shtykh.util.catalogue.Catalogue;
import shtykh.util.catalogue.ListCatalogue;
import shtykh.util.html.ColoredTable;
import shtykh.util.html.HtmlHelper;
import shtykh.util.html.UriGenerator;
import shtykh.util.html.form.build.ActionBuilder;
import shtykh.util.html.form.material.FormMaterial;
import shtykh.util.html.param.Parameter;

import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.Boolean.parseBoolean;
import static shtykh.util.Util.*;
import static shtykh.util.html.HtmlHelper.href;
import static shtykh.util.html.HtmlHelper.htmlPage;
import static shtykh.util.html.form.build.FormBuilder.buildUploadForm;
import static shtykh.util.html.form.param.FormParameterType.*;

/**
 * Created by shtykh on 01/10/15.
 */
public class Pack extends ListCatalogue<Question> implements FormMaterial, _4Sable, Authored, UriGenerator {
	private static final Logger log = Logger.getLogger(Pack.class);
	private final String id;
	private QuestionNumerator numerator;
	private final HtmlHelper htmlHelper;
	private final AuthorsCatalogue authors;
	private PackInfo info;

	private ActionBuilder editQuestionAction;
	private ActionBuilder editAuthorAction;
	private ActionBuilder editPackAction;
	private ActionBuilder addEditorAction;

	public Pack(String id, HtmlHelper htmlHelper, AuthorsCatalogue authors) throws FileNotFoundException {
		super(Question.class, Util.readProperty("quedit.properties", "packs") + "/" + id);
		this.id = id;
		setName(id);
		this.htmlHelper = htmlHelper;
		this.authors = authors;
		initActions();
	}

	public Response home() {
		refresh();
		ColoredTable questionsTable;
		URI uriList;
		URI uriNew;
		URI uriText;
		URI uriBuild;
		URI uriAuthors;
		URI uriUploadForm;
		try {
			questionsTable = getQuestionTable();
			uriList = uri("");
			Parameter<String> parameter = new Parameter<>("index", String.valueOf(size()));
			uriNew = uri("editForm", parameter);
			uriText = uri("text");
			String outFormat = readProperty("quedit.properties", "outFormat");
			Boolean	debug = parseBoolean(readProperty("quedit.properties", "debug"));
			uriBuild = uri("compose",
					new Parameter<>("outFormat", outFormat),
					new Parameter<>("debug", debug.toString()));
			uriAuthors = uri("/quedit/rest/authors/list");
			uriUploadForm = uri("uploadForm", new Parameter<>("what", "4s"));

		} catch (Exception e) {
			return Response.status(500).entity(e.toString()).build();
		}
		String href = href(uriList, getName());
		String body =
						href(uriText, "Полный текст в 4s") + "<br>" +
						href(uriUploadForm, "Импорт из 4s") + "<br>" +
						href(uriBuild, "Сгенерировать пакет") + "<br>" +
						folder.getAbsolutePath() + "<br>" +
						questionsTable.toString() + "<br>" +
						href(uriNew, "Добавить вопрос №" + numerator.getNumber(size())) + "<br><br>" +
						editPackAction.buildForm(this) + "<br>" +
						addEditorAction.buildForm(authors) + "<br>" +
						href(uriAuthors, "Каталог авторов") + "<br>" +
						"";
		return Response.status(Response.Status.OK).entity(htmlPage(getName(), href, body)).build();
	}

	private void initActions() {
		editQuestionAction = new ActionBuilder(address("edit"));
		editAuthorAction = new ActionBuilder(address("editAuthor"));
		editPackAction = new ActionBuilder(address("editPack"));
		addEditorAction = new ActionBuilder(address("addEditor"));
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
					.addParam(PackInfo.class, "name", "Название пакета", text)
					.addParam(PackInfo.class, "nameLJ", "Название пакета (для ЖЖ)", text)
					.addParam(PackInfo.class, "date", "Дата", text)
					.addParam(PackInfo.class, "editor", "Редакторы", comment)
					.addParam(PackInfo.class, "metaInfo", "Слово редактора", textarea)
			;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		this.info = Jsonable.fromJson(Util.read(infoPath()), PackInfo.class);
	}

	private String infoPath() {
		return folderPath() + ".info";
	}

	public Response editPack(
			 String name,
			 String nameLJ,
			 String date,
			 String metaInfo
	) {
		setName(name);
		setNameLJ(nameLJ);
		setDate(date);
		setMetaInfo(metaInfo);
		return home();
	}

	public Response uploadForm( String what) {
		String action = "upload";
		if (what.equals("4s")) {
			action = "upload4s";
		}
		return Response.ok(htmlPage("Загрузите файл", buildUploadForm(action))).build();

	}

	public Response uploadFile(
			  InputStream fileInputStream,
			  FormDataContentDisposition contentDispositionHeader) {

		String filePath = folder + "/uploads/" +contentDispositionHeader.getFileName();
		saveFile(fileInputStream, filePath);
		String output = "File saved to server location : " + filePath;
		return Response.status(200).entity(output).build();
	}

	public Response upload4s(
			  InputStream fileInputStream,
			  FormDataContentDisposition contentDispositionHeader) {
		String filePath = folder + "/" +contentDispositionHeader.getFileName();
		clearFolder();
		File file = saveFile(fileInputStream, filePath);
		Parser4s parser4s = new Parser4s(filePath);
		this.fromParser(parser4s);
		file.delete();
		return home();
	}

	public void fromParser(Parser4s parser4s) {
		info = parser4s.getInfo();
		info.save(infoPath());
		for (Question question : parser4s.getQuestions()) {
			add(question);
		}
	}

	public Response editForm( int index) {
		Question question = get(index);
		if (question == null) {
			question = Question.mock();
			question.newIndex(size());
		} else {
			question.newIndex(index);
		}
		question.setNumber(numerator.getNumber(question.index()));
		String body = questionHtml(question) + editQuestionAction.buildForm(question);
		return Response
				.status(Response.Status.OK)
				.entity(htmlPage("Отредактируйте вопрос", body))
				.build();
	}

	public Response editAuthorForm( int index) throws URISyntaxException {
		Question question = get(index);
		if (question == null) {
			question = Question.mock();
			question.newIndex(size());
		} else {
			question.newIndex(index);
		}
		question.setNumber(numerator.getNumber(question.index()));
		question.setAuthors(authors);
		String body = questionHtml(question) + "<br>"
				+ editAuthorAction.buildForm(question)
				+ href(htmlHelper.uriBuilder("/quedit/rest/authors/list").build(), "Каталог авторов");
		return Response
				.status(Response.Status.OK)
				.entity(htmlPage("Добавить автора", body))
				.build();
	}

	public Response removeMethod( int index) {
		super.remove(index);
		return home();
	}

	public Response replace(int index) {
		super.replace(index, "запас");
		return home();
	}

	public Response upMethod( int index) {
		super.up(index);
		return home();
	}

	public Response downMethod( int index) {
		super.down(index);
		return home();
	}

  	public Response edit(
			 int index,
			 String unaudible,
			 String color,
			 String text,
			 String answer,
			 String possibleAnswers,
			 String impossibleAnswers,
			 String comment,
			 String sources
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

	public Response editAuthor(
			 int index,
			 String author
	) {
		Question question = get(index);
		question.setAuthors(authors);
		question.addAuthor(author);
		add(index, question);
		return home();
	}

	public Response addEditor(String author) {
		addAuthor(author);
		return home();
	}

	public Response nextColor(
			 int index,
			 String colorHex
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

	public Response text() throws IOException {
		String text4s = to4s();
		return Response.ok(htmlPage(getName(), "", text4s.replace("\n", "<br>"))).build();
	}

	public Response compose( String outFormat,  boolean debug) throws IOException {
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
		//File template = copyFileToDir(readProperty("quedit.properties", "templatedocx"), timestampFolder);
		//logs.debug(template + " was created");
		String[] cmd = chgkComposeCmd("compose", name4sFile, outFormat, "--nospoilers");
		if (call(logs, cmd) == 0) {
			logs.info("Файл формата " + outFormat + " успешно создан в папке " + timestampFolder);
			if (outFormat.equals("docx")) {
				String path = getPath(timestampFolder, outFormat);
				try {
					URI downloadHref = uri("download/docx", new Parameter<>("path", path));
					logs.info(href(downloadHref, "Скачать"));
				} catch (Exception e) {
					logs.error(e.getMessage());
				}
			}
		}
		//template.delete();
		//logs.debug(template + " was deleted");
		return Response.ok(htmlPage("Выгрузка", logs.toString().replace("\n", "<br>"))).build();
	}

	public Response downloadDocFile( String path) {
		File file = new File(path);
		Response.ResponseBuilder responseBuilder = Response.ok(file);
		responseBuilder.header("Content-Disposition", "attachment; filename=\"" + id + ".docx\"");
		return responseBuilder.build();
	}
	
	public String base(){
		return "/quedit/rest/pack/" + id;
	}
	
	public HtmlHelper htmlHelper() {
		return htmlHelper;
	}

	private ColoredTable getQuestionTable() throws URISyntaxException {
		ColoredTable table = new ColoredTable("Номер", "Ответ", "Редактировать", "Авторы", "В запас", "Вверх", "Вниз");
		for (int i = 0; i < size(); i++) {
			String index = String.valueOf(i);
			Parameter<String> parameter = new Parameter<>("index", index);
			URI home = uri("");
			URI uriEdit = uri("editForm", parameter);
			String questionColor = get(i).getColor();
			URI uriColor = uri("nextColor", parameter, new Parameter<>("color", questionColor));
			URI uriEditAuthor = uri("editAuthorForm", parameter);
			URI uriUp = i == 0 ? home : uri("up", parameter);
			URI uriDown = i == size() - 1 ? home : uri("down", parameter);
			URI uriRemove = uri("remove", parameter);
			URI uriReplace = uri("replace", parameter);
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
		initInfo();
	}

	private void initInfo() {
		try {
			this.info = Jsonable.fromJson(Util.read(infoPath()), PackInfo.class);
		} catch (Exception e) {
			info = new PackInfo();
			info.save(infoPath());
		}
		if (StringUtils.isBlank(getName())) {
			setName(id);
		}
	}

	private String questionHtml(Question question) {
		return question.toString().replace("\n", "<br>") + "<br>";
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

	public void addAuthor(String name) {
		if (authors == null) {
			throw new RuntimeException("Authors were null");
		}
		info.addAuthor(authors.get(name));
		info.save(infoPath());
	}

	private String getPath(File folder, String extension) {
		if (folder == null || !folder.isDirectory()) {
			return null;
		} else {
			for (File file : folder.listFiles()) {
				if (file.getName().endsWith(extension)) {
					return file.getAbsolutePath();
				}
			}
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		Pack pack = new Pack("pack0", new HtmlHelper(), new AuthorsCatalogue());
		//System.out.println(pack.home().getEntity());
		System.out.println(pack.compose("docx", true).getEntity());
	}

	private static String[] chgkComposeCmd(String... parameters) throws FileNotFoundException {
		String[] cmd = new String[parameters.length + 2];
		cmd[0] = readProperty("quedit.properties", "python");
		cmd[1] = readProperty("quedit.properties", "chgksuite");
		for (int i = 0; i < parameters.length; i++) {
			cmd[i + 2] = parameters[i];
		}
		return cmd;
	}

	@Override
	public String to4s() {
		StringBuilder sb = new StringBuilder();
		append(sb, info._4sName());
		append(sb, info._4sNameLJ());
		append(sb, info._4sDate());
		append(sb, info._4sAuthor());
		append(sb, info._4sMetaInfo());
		for (Question question : super.getAll()) {
			numerator.renumber(question);
			sb.append(question.to4s() + "\n");
		}
		return sb.toString();
	}

	@Override
	public Person getAuthor() {
		return info.getAuthor();
	}
	@Override
	public void setAuthor(MultiPerson author) {
		info.setAuthor(author);
		info.save(infoPath());
	}

	public String getMetaInfo() {
		return info.getMetaInfo();
	}

	public void setMetaInfo(String metaInfo) {
		info.setMetaInfo(metaInfo);
		info.save(infoPath());
	}

	public void setName(String name) {
		info.setName(name);
		info.save(infoPath());
	}

	public String getNameLJ() {
		return info.getNameLJ();
	}

	public void setNameLJ(String nameLJ) {
		info.setNameLJ(nameLJ);
		info.save(infoPath());
	}

	public String getDate() {
		return info.getDate();
	}

	public void setDate(String date) {
		info.setDate(date);
		info.save(infoPath());
	}

	public String getName() {
		return info.getName();
	}
}
