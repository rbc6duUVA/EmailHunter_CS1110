import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//Ben Greenawald and Ben Canty
//Computing ID: bhg5yd
//This code is a little buggy, even with the basic emails. Just look at the output and see if you can figure it out
public class EmailHunter {

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		//System.out.println("Is working");
		//File file = new File("email_hunt.txt");
		//Scanner scan1 = new Scanner(file);
		
		//Gets URL from the user
		System.out.println("Please enter a URL");
		String url1 = scan.nextLine();
		URL url = new URL(url1);
		Scanner scan1 = new Scanner(url.openStream());
		
		ArrayList<String> emails = new ArrayList<String>();
		
		//Scan the whole webpage into a single readable string
		String whole = "";
		
		while(scan1.hasNextLine()){
			whole += scan1.nextLine() + "\n";
		}
		
		//More robust method for getting emails in the standard form
		for(int i = 0; i < whole.length(); i++){
			if(whole.charAt(i) == '@'){
				int start = whole.lastIndexOf(' ', i);
				int finish = whole.indexOf(' ', i);
				if(whole.charAt(finish - 1) == '.'){
					while(!(emails.contains(whole.substring(start, finish - 1))))	//Removes the period if it is puntuation
					emails.add(whole.substring(start, finish - 1));
					}
				else{
					while(!(emails.contains(whole.substring(start, finish))))		//Leaves it otherwise
						emails.add(whole.substring(start, finish));
				}
			}
		}
		
		for(int i = 0; i < emails.size(); i++){
			System.out.println("Found: " + emails.get(i));
		}

}}
