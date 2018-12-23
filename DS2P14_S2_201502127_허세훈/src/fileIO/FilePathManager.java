package fileIO;

import java.io.File;

public final class FilePathManager {

	private FilePathManager() {
		
	}
	
	public static String getFileExtension(File file) {
		String fileName = file.getName();
		int lastIndexOf = fileName.lastIndexOf(".");
		if(lastIndexOf == -1) {
			return "";
		}
		else {
			return fileName.substring(lastIndexOf);
		}
	}
	
	public static String getFilePathAndNameWithoutExtension(File file) {
		String filePathAndName = file.getAbsolutePath();
		int lastIndexOf = filePathAndName.lastIndexOf(".");
		if(lastIndexOf == -1) {
			return filePathAndName;
		}
		else {
			return filePathAndName.substring(0, lastIndexOf);
		}
	}
}
