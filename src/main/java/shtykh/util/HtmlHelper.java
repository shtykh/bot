package shtykh.util;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shtykh on 03/04/15.
 */
public class HtmlHelper {

	private final String host;
	private final int port;

	public HtmlHelper(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public URIBuilder uriBuilder(String postfix, Parameter... parameters) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder()
				.setHost(host)
				.setPort(port)
				.setPath(postfix);
		for (Parameter parameter : parameters) {
			uriBuilder.addParameter(parameter.getName(), String.valueOf(parameter.getValue()));
		};
		return uriBuilder;
	}

	public static String href(URI uri) {
		return href(uri, null);
	}

	public static String href(URI uri, String name) {
		String href = uri.toString();
		if (name == null) {
			name = href;
		}
		return "<a href=" + href + ">" + name + "</a>";
	}

	public static String htmlPage(String title, String body) {
		return new HtmlBuilder()
				.title(title)
				.body(body)
				.build();
	}

	public static String htmlPage(String title, String header, String body) {
		return new HtmlBuilder()
				.title(title)
				.header(header)
				.body(body)
				.build();
	}

	public static String errorPage(String msg) {
		return htmlPage("Error", msg);
	}

	public URI getHome() {
		try {
			return uriBuilder("Error").build();
		} catch (URISyntaxException e) {
			throw new RuntimeException();
		}
	}

	private static class HtmlBuilder {
		private static final String DEFAULT_TITLE = "Untitled";
		private static final String DEFAULT_BODY = "";
		private static final String DEFAULT_CHARSET = "UTF-8";
		private String title;
		private String header;
		private String body;
		private String charset;

		public HtmlBuilder title(String title) {
			this.title = title;
			return this;
		}

		public HtmlBuilder header(String header) {
			this.header = header;
			return this;
		}

		public HtmlBuilder body(String body) {
			this.body = body.replace("\n", "<br/>");
			return this;
		}

		public String build() {
			assignDefaultIfNull();
			return tag("html", 
						tag("head",
								tag("title", title) +
								"<meta charset=\"" + charset + "\">"
						) +
						tag("body",
								tag("h1", header) + body
						)
					);
		}

		private void assignDefaultIfNull() {
			if (title == null) {
				title = DEFAULT_TITLE;
			}
			if (header == null) {
				header = title;
			}
			if (body == null) {
				body = DEFAULT_BODY;
			}
			if (charset == null) {
				charset = DEFAULT_CHARSET;
			}
		}
	}
	
	public static class FormBuilder {
		private String action;
		private List<Parameter> parameters = new ArrayList<>();
		private List<String> comments = new ArrayList<>();

		public FormBuilder() {
		}
		
		public FormBuilder addParameter(String name, Object value) {
			parameters.add(new Parameter(name, value));
			return this;
		}

		public String build(){
			StringBuilder sb = new StringBuilder();
			for(String comment: comments) {
				sb.append(tag("p", comment));
			}
			for(Parameter parameter: parameters) {
				sb.append(tag("p", parameter.getName() + ":" + 
						input(	"text",
								parameter.getName(),
								parameter.getValue().toString())));
			}
			sb.append(input("submit", "Submit"));
			String form = sb.toString();
			return tag("form", form, 
					new Parameter("method", "get"),
					new Parameter("action", action)
					);
		}

		private String input(String type, String name, String value) {
			return tag("input",
					new Parameter("type", type),
					new Parameter("name", name),
					new Parameter("value", value));
		}

		private String input(String type, String value) {
			return tag("input",
					new Parameter("type", type),
					new Parameter("value", value));
		}

		public FormBuilder setAction(String action) {
			this.action = action;
			return this;
		}

		public FormBuilder addComment(String comment) {
			comments.add(comment);
			return this;
		}
	}

	public static String tag(String tag, Parameter... parameters) {
		return tag(tag, null, parameters); 
	}

	public static String tag(String tag, Object value, Parameter... parameters) {
		StringBuilder sb = new StringBuilder("<" + tag + " ");
		for (Parameter parameter : parameters) {
			sb.append(parameter.toString() + " ");
		}
		if(value != null) {
			sb.append(">" + value + "</" + tag + ">");
		} else {
			sb.append("/>");
		}
		return sb.toString();
	}
}
