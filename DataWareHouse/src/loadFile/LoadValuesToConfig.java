package loadFile;

import java.io.File;
import java.util.ArrayList;

import FileToData.ConfigConnection;
import model.MyConfig;

public class LoadValuesToConfig {
	
	public LoadValuesToConfig() {
		
	}
	
	
	public void run() {
		LoadFile loadFile = new LoadFile();
		ArrayList<File> listFile = loadFile.listAllFile();
		
		ConfigConnection configConnection = new ConfigConnection();
		for (int i = 0; i < listFile.size(); i++) {
			MyConfig config = new MyConfig();
			config.setTablename(loadFile.getName().get(i));
			config.setSourcePath(loadFile.getPathFileFromList().get(i));
			config.setFilename(loadFile.getNameFileFromList().get(i));
			configConnection.insertValues(config);
			
		}
		
	}
	public static void main(String[] args) {
		LoadValuesToConfig loadValuesToConfig = new LoadValuesToConfig();
		loadValuesToConfig.run();
	}

}
