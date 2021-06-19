package compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

	static ArrayList<String> path = new ArrayList<String>();
	static UI ui = new Cmd();
	
	public static void main(String[] args) {
		String pathA = ui.askFolder1();
		String pathB = ui.askFolder2();
		try {
			collectPaths(new File(pathA));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> a = path;
		path = new ArrayList<String>();
		try {
			collectPaths(new File(pathB));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> b = path;
		if (a.size() == b.size()) {
			int nr = 0;
			for (int i = 0; i < a.size(); i++) {
				if (Compare.compareFiles(new File(a.get(i)), new File(b.get(i)))) {
					nr++;
				}
			}
			if (nr == a.size()) {
				ui.printFinding("The folders are the same");
			} else {
				ui.printFinding("The folders are not the same");
			}
		} else {
			ui.printFinding("The folders are not the same");
		}
	}
	
	public static class Compare{
		
		private static byte[] makeFileToByteArr(File currentFile) {
			byte[] fileInBytes = null;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(currentFile);
				long size = currentFile.length();
				fileInBytes = new byte[(int) size];
				int by, i = 0;
				while ((by = fis.read()) != -1) {
					fileInBytes[i++] = (byte) by;
				}
			} catch (IOException ex) {
				System.out.println(ex);
				System.out.println("Cant't make file to bytes.");
				System.exit(0);
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			}
			return fileInBytes;
		}
		
		static boolean compareFiles(File a, File b) {
			ui.checkFolder(a.getAbsolutePath(), b.getAbsolutePath());
			byte[] b_a = makeFileToByteArr(a), b_b = makeFileToByteArr(b);
			boolean theSame = (a.length() == b.length());
			if (theSame) {
				for (int i = 0; i < b_b.length; i++) {
					if (!(b_a[i] == b_b[i])) {
						theSame = false;
						break;
					}
				}
			}
			return theSame;
		}
	}
	
	static void collectPaths(File from) throws IOException {
		File[] fileList = from.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (file.isDirectory()) {
					collectPaths(file);
				} else {
					path.add(file.getAbsolutePath());
				}
			}
		}
	}
}