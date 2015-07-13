package shtykh.util;

import shtykh.parrots.what.CommaSeparatedValues;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shtykh on 10/07/15.
 */
public abstract class StringSerializer<T> {
	private static Map<Class, StringSerializer> map = new ConcurrentHashMap<>();
	static {
		map.put(Integer.class, 	new StringSerializer<Integer>() {
			@Override
			public Integer fromString(String string) {
				return Integer.decode(string);
			}

			@Override
			public String toString(Integer stringSerializable) {
				return stringSerializable.toString();
			}
		});
		map.put(Long.class, new StringSerializer<Long>() {
			@Override
			public Long fromString(String string) {
				return Long.decode(string);
			}

			@Override
			public String toString(Long stringSerializable) {
				return stringSerializable.toString();
			}
		});
		
		map.put(Date.class, new StringSerializer<Date>() {
			private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			@Override
			public Date fromString(String string) {
				try {
					return df.parse(string);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String toString(Date time) {
				return df.format(time);
			}
		});
		map.put(Boolean.class, 	new StringSerializer<Boolean>() {
			@Override
			public Boolean fromString(String string) {
				return Boolean.valueOf(string);
			}

			@Override
			public String toString(Boolean stringSerializable) {
				return stringSerializable.toString();
			}
		});
		
		map.put(CommaSeparatedValues.class, new StringSerializer<CommaSeparatedValues>() {
			@Override
			public CommaSeparatedValues fromString(String string) {
				return new CommaSeparatedValues(string);
			}

			@Override
			public String toString(CommaSeparatedValues csv) {
				return csv.toString();
			}
		});
		
	}

	public abstract T fromString(String string);
	public abstract String toString(T stringSerializable);

	public static <T> StringSerializer<T> getForClass(Class<T> clazz) {
		return map.get(clazz);
	}
}
