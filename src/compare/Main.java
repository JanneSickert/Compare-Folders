package compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

	static ArrayList<String> path = new ArrayList<String>();
	static UI ui = new Cmd();
	static boolean[] same;
	final static int NR_OF_THREADS = 7;
	static MyThread[] thread = new MyThread[NR_OF_THREADS];
	static int threadIndex = 0;

	public static void main(String[] args) {
		String pathA = ui.askFolder1();
		String pathB = ui.askFolder2();
		for (int i = 0; i < NR_OF_THREADS; i++) {
			thread[i] = null;
		}
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
			same = new boolean[a.size()];
			for (int i = 0; i < a.size(); i++) {
				compareFiles(new File(a.get(i)), new File(b.get(i)), i);
			}
			joinAll();
			if (checkSame()) {
				ui.printFinding("The folders are the same");
			} else {
				ui.printFinding("The folders are not the same");
			}
		} else {
			ui.printFinding("The folders are not the same");
		}
	}

	static void joinAll() {
		for (int i = 0; i < NR_OF_THREADS; i++) {
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return true if the folders are the same.
	 */
	static boolean checkSame() {
		int nr = 0;
		for (int i = 0; i < same.length; i++) {
			if (same[i])
				nr++;
		}
		return (nr == same.length);
	}

	static void compareFiles(File a, File b, int sameIndex) {
		if (thread[threadIndex] == null) {
			thread[threadIndex] = new MyThread(a, b, sameIndex);
			thread[threadIndex].start();
			threadIndex++;
			if (threadIndex == NR_OF_THREADS) {
				threadIndex = 0;
			}
		} else {
			boolean bb = true;
			do {
				if (!(thread[threadIndex].isAlive())) {
					bb = false;
					thread[threadIndex] = new MyThread(a, b, sameIndex);
					thread[threadIndex].start();
				}
				threadIndex++;
				if (threadIndex == NR_OF_THREADS) {
					threadIndex = 0;
				}
			} while (bb);
		}
	}
	
	static synchronized void setSame(int sameIndex, boolean theSame) {
		Main.same[sameIndex] = theSame;
	}

	public static class MyThread extends Thread{
		
		File a, b;
		int sameIndex = -1;
		final long MAX_SPACE = Integer.MAX_VALUE - 255;
		
		MyThread(File a, File b, int sameIndex) {
			this.a = a;
			this.b = b;
			this.sameIndex = sameIndex;
		}
		
		@Override
		public void run() {
			ui.checkFolder(a.getAbsolutePath(), b.getAbsolutePath());
			long len_a = a.length(), len_b = b.length();
			boolean theSame = (len_a == len_b);
			if (theSame) {
				if (len_a < MAX_SPACE) {
					byte[] b_a = makeFileToByteArr(a), b_b = makeFileToByteArr(b);
					for (int i = 0; i < b_b.length; i++) {
						if (!(b_a[i] == b_b[i])) {
							theSame = false;
							break;
						}
					}
				} else {
					try {
						FileInputStream is_a = new FileInputStream(a);
						FileInputStream is_b = new FileInputStream(b);
						int ret1 = 0, ret2 = 0;
						do {
							ret1 = is_a.read();
							ret2 = is_b.read();
							if (ret1 != ret2) {
								theSame = false;
								break;
							}
							System.out.println("buffer1:" + ret1);
							System.out.println("buffer2:" + ret2);
						} while (ret1 > 0 && ret2 > 0);
						is_a.close();
						is_b.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						System.exit(1);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(2);
					}
				}
			}
			Main.setSame(sameIndex, theSame);
		}
		
		private byte[] makeFileToByteArr(File currentFile) {
			Path path = Paths.get(currentFile.getAbsolutePath());
			try {
				return (Files.readAllBytes(path));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
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