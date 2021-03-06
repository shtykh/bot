package shtykh.util.html;

import org.apache.http.client.utils.URIBuilder;
import shtykh.util.html.param.Parameter;

import java.net.URI;
import java.net.URISyntaxException;

import static shtykh.util.html.TagBuilder.tag;

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
			uriBuilder.addParameter(parameter.getName(), parameter.getValueString());
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
		return tag("a")
				.params(new Parameter<>("href", href))
				.build(name);
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

	public static String colorTag(String value, String color) {
		return tag("font")
				.params(new Parameter<>("color", color))
				.build(value);
	}

	private static class HtmlBuilder {
		private static final String DEFAULT_TITLE = "Untitled";
		private static final String DEFAULT_BODY = "";
		private static final String DEFAULT_CHARSET = "UTF-8";
		private static final String DEFAULT_STYLE = "";
		private String title;
		private String header;
		private String body;
		private String charset;
		private String style;

		public HtmlBuilder title(String title) {
			this.title = title;
			return this;
		}

		public HtmlBuilder header(String header) {
			this.header = header;
			return this;
		}
		
		public HtmlBuilder style(String value) {
			this.style = value;
			return this;
		}

		public HtmlBuilder body(String body) {
			this.body = body;
			return this;
		}

		public String build() {
			assignDefaultIfNull();
			Parameter<String> charsetParam = new Parameter<>("charset", charset);
			String head = tag("title").build(title)
					+ tag("meta").params(charsetParam)
					+tag("style").build(style);
			String body = tag("h1").build(header) + this.body;
			return tag("html").build(
						tag("head").build(head) +
						tag("body").build(body));
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
			if (style == null) {
				style = DEFAULT_STYLE;
			}
		}
	}
}
