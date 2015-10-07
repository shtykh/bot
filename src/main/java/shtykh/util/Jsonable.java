package shtykh.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by shtykh on 02/10/15.
 */
public interface Jsonable {
	ObjectMapper mapper = new ObjectMapper();
	
	public default String toJson() {
		try {
			return mapper.writeValueAsString(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends Jsonable> T fromJson(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage() + " in:\n" + json);
		}
	}
}
