package shtykh.util.catalogue;

import shtykh.parrots.what.CSV;
import shtykh.util.Jsonable;
import shtykh.util.html.form.material.FormMaterial;
import shtykh.util.html.form.material.FormParameterMaterial;

import java.io.File;

import static shtykh.util.Jsonable.fromJson;
import static shtykh.util.Util.read;
import static shtykh.util.Util.readProperty;

/**
 * Created by shtykh on 02/10/15.
 */
public abstract class Catalogue<K,T extends Jsonable> implements FormMaterial {
	private final Class<T> clazz;
	protected File folder;
	private final String propertyName;
	protected FormParameterMaterial<CSV> keys = new FormParameterMaterial<>(new CSV(""), CSV.class);
	

	public Catalogue(Class<T> clazz, String propertyName) {
		this.clazz = clazz;
		this.propertyName = propertyName;
		initFolder();
		initFields();
		refresh();
	}

	protected abstract void initFields();

	private void initFolder() {
		try {
			String filename = readProperty("quedit.properties", propertyName);
			folder = new File(filename);
			if (!folder.exists()) {
				folder.mkdirs();
			} else if (!folder.isDirectory()) {
				throw new Exception(filename + " must be a directory!");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void refresh() {
		clear();
		for(File file: folder.listFiles()) {
			if (!file.isDirectory() && ! file.getName().startsWith(".")) {
				T p = fromJson(read(file), clazz);
				add(p);
			}
		}
		refreshKeys();
	}

	public String[] getKeys() {
		refresh();
		return keys.get().asArray();
	}

	protected abstract void add(T p);

	protected File file(K name) {
		return new File(folder.getAbsolutePath() + "/" + name);
	}

	protected abstract void clear();


	public void remove(K name) {
		file(name).delete();
		refresh();
	}

	public void replace(K name, String folder) {
		file(name).renameTo(new File(this.folder.getAbsolutePath().replace(this.folder.getName(), folder)));
		refresh();
	}

	protected abstract void refreshKeys();
	protected abstract K getFileName(T p);
	public abstract void add(K number, T item);
	public abstract T get(K key);

	protected abstract int size();
}
