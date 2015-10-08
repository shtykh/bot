package shtykh.util.catalogue;

import shtykh.util.CSV;
import shtykh.util.Jsonable;
import shtykh.util.html.form.material.FormMaterial;
import shtykh.util.html.form.material.FormParameterMaterial;

import java.io.File;

import static shtykh.util.Jsonable.fromJson;
import static shtykh.util.Util.read;

/**
 * Created by shtykh on 02/10/15.
 */
public abstract class Catalogue<K,T extends Jsonable> extends FolderKeaper implements FormMaterial {
	private final Class<T> clazz;
	protected FormParameterMaterial<CSV> keys = new FormParameterMaterial<>(new CSV(""), CSV.class);
	

	public Catalogue(Class<T> clazz, String fileName) {
		super(fileName);
		this.clazz = clazz;
		initFields();
		refresh();
	}

	protected abstract void initFields();

	@Override
	public void refresh() {
		super.refresh();
		refreshKeys();
	}

	@Override
	public void refreshFile(File file) {
		T p = fromJson(read(file), clazz);
		add(p);
	}

	@Override
	public boolean isGood(File file) {
		return !file.isDirectory() && ! file.getName().startsWith(".");
	}

	public String[] keys() {
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
