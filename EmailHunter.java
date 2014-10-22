import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//Ben Greenawald and Ben Canty
//Computing ID: bhg5yd
//
public class EmailHunter {
	
	public static ArrayList<String> removehyperlink(ArrayList<String> s){
		//First draft of code to remove hyperlinks
		
		for(int i = 0; i < s.size(); i++){
			while(s.get(i).contains("<br>")){
				String temp = s.get(i);
				s.remove(i);
				temp = temp.replace("<br>", "");
				s.add(temp);
				
			}
		}
		for(int i = 0; i < s.size(); i++){
			while(s.get(i).contains("</a>")){
				String temp = s.get(i);
				s.remove(i);
				temp = temp.replace("</a>", "");
				s.add(temp);
				
			}
		}
		for(int i = 0; i < s.size(); i++){ //THIS IS BUGGY AND DOES NOT WORK YET
			while(s.get(i).contains("href=")){
				int c = s.get(i).indexOf('@');
				int start = s.get(i).lastIndexOf('"', c);
				int end = s.get(i).indexOf('"',c);
				String temp = s.get(i).substring(start + 1, end);
				s.remove(i);
				while(!(s.contains(temp)))
					s.add(temp);
			
			}
		}	
		
		return s;
	}

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		
		//File file = new File("email_hunt.txt");
		//Scanner scan1 = new Scanner(file);
		
		//Gets URL from the user
		System.out.print("Please enter a URL: ");
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
		
		//Remove hyperlinks
		emails = EmailHunter.removehyperlink(emails);
		
		//Removes invalid addresses that contain @ but are not emails addresses
		for(int i = 0; i < emails.size(); i++){
			System.out.println(emails.get(i));
			int c = emails.get(i).indexOf('@');
			int size = emails.get(i).length();
			String end = emails.get(i).substring(c, size);
			
			if(end.length() < 2){
				emails.remove(i);
			}
		}
		for(int i = 0; i < emails.size(); i++){
			System.out.println("Found: " + emails.get(i));
		}

}}
