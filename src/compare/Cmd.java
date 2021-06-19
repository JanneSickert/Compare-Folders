package compare;

import java.util.Scanner;

public class Cmd implements UI{

	@Override
	public String askFolder1() {
		@SuppressWarnings("resource")
		final Scanner sc = new Scanner(System.in);
		System.out.print("Path folder 1:");
		String answer = sc.nextLine();
		System.out.println("\n");
		return answer;
	}

	@Override
	public String askFolder2() {
		@SuppressWarnings("resource")
		final Scanner sc = new Scanner(System.in);
		System.out.print("Path folder 2:");
		String answer = sc.nextLine();
		System.out.println("\n");
		return answer;
	}

	@Override
	public void printFinding(String s) {
		System.out.println(s);
	}

	@Override
	public void checkFolder(String a, String b) {
		System.out.println("Compare Folder " + a + " with " + b);
	}
}