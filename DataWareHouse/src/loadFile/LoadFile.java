package loadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class LoadFile {
	private File folder;
	ArrayList<String> listName;
	ArrayList<String> listPath;

	public LoadFile() {
		this.folder = new File("D:\\GitHub\\DataWareHouse\\DataWareHouse\\ListFileDownload");
		this.listName = getName();
		this.listPath = getPathFileFromList();
	}

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}
	
	
	
	
	
	public ArrayList<File> listAllFile() {
		ArrayList<File> listFile  = new ArrayList<File>();
		File[] files = this.folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			listFile.add(files[i]);
		}
		return listFile;
	}
	public ArrayList<String> getNameFileFromList() {
		ArrayList<File> listFile = listAllFile();
		ArrayList<String> listName = new ArrayList<String>();
		for (File file : listFile) {
			listName.add(file.getName());
		}
		return listName;
	}
	public ArrayList<String> getPathFileFromList() {
		ArrayList<File> listFile = listAllFile();
		ArrayList<String> listPath = new ArrayList<String>();
		for (File file : listFile) {
			String pathFile ="";
			StringTokenizer stringtol = new StringTokenizer(file.getParent(),"\\");
			while(stringtol.hasMoreTokens()) {
				pathFile+=stringtol.nextToken()+"\\"+"\\";
			}
			listPath.add(pathFile);
		}
		
		return listPath;
	}
	
	public ArrayList<String> getName(){
		ArrayList<String> list = getNameFileFromList();
		ArrayList<String> listName = new ArrayList<String>();
		for (String string : list) {
			StringTokenizer nameL = new StringTokenizer(string,".");
			String name = nameL.nextToken();
				listName.add(name);
			
		}
		return listName;
	}
	public static void main(String[] args) {
		LoadFile loadFile = new LoadFile();
		ArrayList<String> list = loadFile.getPathFileFromList();
		
		for (String string : list) {
			System.out.println(string);
		}
	}

}
