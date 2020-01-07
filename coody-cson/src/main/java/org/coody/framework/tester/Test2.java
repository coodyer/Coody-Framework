package org.coody.framework.tester;

public class Test2 {

	public static void main(String[] args) {
		String string = "abcdefg";
		String newString = reverse(string);
		System.out.println(newString);
	}

	private static String reverse(String string) {
		return new StringBuilder(string).reverse().toString();
	}
}
