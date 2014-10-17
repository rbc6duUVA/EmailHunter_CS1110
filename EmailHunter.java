import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//Ben Greenawald and Ben Canty
//Computing ID: bhg5yd
//
public class EmailHunter {

	public static void main(String[] args) throws Exception {
		//Scanner scan = new Scanner(System.in);
		System.out.println("Is working");
		File file = new File("email_hunt.txt");
		Scanner scan1 = new Scanner(file);
		
		//Gets URL from the user
		//System.out.println("Please enter a URL");
		//String url1 = scan.nextLine();
		//URL url = new URL(url1);
		//Scanner scan1 = new Scanner(url.openStream());
		
		ArrayList<String> emails = new ArrayList<String>();
		
		//Scan the whole webpage into a single readable string
		String whole = "";
		
		while(scan1.hasNextLine()){
			whole += scan1.nextLine() + "\n";
		}
		
		//Returns the emails that are in the most standard form e.g hello@virginia.edu
		while(whole.contains("@")){

			int j = whole.lastIndexOf(' ');
			int k = whole.indexOf(' ');
			
			emails.add(whole.substring(j + 1, k));
		}

		System.out.println(emails.toString());
	}

}
