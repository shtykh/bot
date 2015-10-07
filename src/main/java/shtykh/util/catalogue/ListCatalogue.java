package shtykh.util.catalogue;

import shtykh.parrots.what.CSV;
import shtykh.quedit.numerator.NaturalNumerator;
import shtykh.quedit.numerator.Numerator;
import shtykh.util.Jsonable;
import shtykh.util.Util;
import shtykh.util.html.form.material.FormParameterMaterial;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shtykh on 05/10/15.
 */
public abstract class ListCatalogue<T extends Jsonable> extends Catalogue<Integer, T> {
	private List<T> list;
	private Numerator numerator;

	public ListCatalogue(Class<T> clazz, String propertyName) {
		super(clazz, propertyName);
	}
	
	protected void refreshKeys() {
		keys.set(numerator.firstNumbers(size()));
	}

	protected void swap(int key, int key2) {
		Collections.swap(list, key, key2);
		File buffer = file(-1);
		file(key).renameTo(buffer);
		file(key2).renameTo(file(key));
		buffer.renameTo(file(key2));
	}

	@Override
	protected void clear() {
		list.clear();
	}

	@Override
	protected void initFields() {
		list = new ArrayList<>();
		keys = new FormParameterMaterial<>(new CSV(""), CSV.class);
		numerator = new NaturalNumerator(1);
	}

	@Override
	public void add(Integer number, T item) {
		if (number >= size()) {
			list.add(item);
		}
		Util.write(file(number), item.toJson());
	}

	@Override
	public T get(Integer key) {
		try{
			return list.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	public void up(Integer key) {
		swap(key, key - 1);
	}

	public void down(Integer key) {
		swap(key, key + 1);
	}

	@Override
	protected int size() {
		return list.size();
	}

	@Override
	protected Integer getFileName(T p) {
		return list.indexOf(p);
	}

	public List<T> getList() {
		return list;
	}

	@Override
	protected void add(T p) {
		add(size(), p);
	}
}
