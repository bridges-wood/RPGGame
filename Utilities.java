import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Utilities {
	
	public static String randomLine(File text) throws FileNotFoundException{
		//Takes an input text file and when a randomly generated integer is equal to 0, returns a line of the text file.
		Random rand = new Random();
		int n = 0;
		for(Scanner sc = new Scanner(text); sc.hasNext();) {
			++n;
			if(rand.nextInt(n) == 0) {
				sc.close(); 
				return sc.nextLine();
			}
		}
		return "";
	}
	
	public static String StrInput() {
		//Takes string input from the console.
		Scanner in = new Scanner(System.in);
		String out = "";
		out = in.nextLine();
		in.close();
		return out;
	}
	
	public static String StrReplace(String str, int index, char newChar) {
		//Takes an input string and changes a character at a given index.
		StringBuilder out = new StringBuilder(str);
		out.setCharAt(index, newChar);
		return out.toString();
	}
	
	public static int rangedRandomInt(int min, int max) {
		return (int) (Math.random() * ((max - min) + 1 )) + min;
	}
	
	public static String generatePassword(int difficulty) throws FileNotFoundException {
		/*Generates random passwords using random words from a dictionary file and adding 
		  a number of non-alphanumeric visible characters based on the difficulty.*/
		File file = new File("StatRes/Dictionary.txt");
		String base = Utilities.randomLine(file);
		for(int i = 0; i < difficulty / 3; i++) {
			int index = Utilities.rangedRandomInt(base.length() - 1, 0);
			Utilities.StrReplace(base, index, (char) Utilities.rangedRandomInt(33, 63));
		}
		return base;
	}
}