package shtykh.util.catalogue;

import java.io.File;

/**
 * Created by shtykh on 08/10/15.
 */
public abstract class FolderKeaper {
	protected File folder;

	public FolderKeaper(String folderName) {
		initFolder(folderName);
	}

	private void initFolder(String filename) {
		try {
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

	public void refresh() {
		clear();
		for(File file: folder.listFiles()) {
			if (isGood(file)) {
				refreshFile(file);
			}
		}
	}

	protected abstract void clear();

	public abstract void refreshFile(File file);

	public abstract boolean isGood(File file);

	public String folderPath() {
		return folder.getAbsolutePath();
	}
}
