package myphotos.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {
	private Properties prop;

	AppProperties() {
		File file = new File("config.properties");
		if (!file.exists()) {
			prop = createPropFile();
		} else {
			prop = readProperties();
		}
	}

	private Properties createPropFile() {
		Properties newProp = new Properties();
		try {			
			newProp.setProperty("menu.file", "  File  ");
			newProp.setProperty("menu.open", "Open Image");
			newProp.setProperty("menu.scanfolder", "Scan folder");
			newProp.setProperty("menu.quit", "Quit");
			newProp.setProperty("label.tags", "Tags");
			newProp.setProperty("label.Files", "Files");
			newProp.setProperty("button.add", "Add");
			newProp.setProperty("button.delete", "Delete");
			newProp.setProperty("button.edit", "Edit");
			newProp.setProperty("mess.suretoquit", "Close application?");
			newProp.setProperty("mess.titleexit", "Exit Confirmation");
			newProp.store(new FileOutputStream("config.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newProp;
	}

	private Properties readProperties() {
		Properties newProp = new Properties();
		try {
			newProp.load(new FileInputStream("config.properties"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return newProp;
	}
	
	public String getPropertie(String key) {
		return prop.getProperty(key);
	}
}
