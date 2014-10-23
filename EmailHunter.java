// Ben Greenawald (bhg5yd) and Ben Canty (rbc6du)

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class EmailFinder {				//We should refactor the Class name to EmailFinder (Because that's the .java file the AutoGrading system expects

	public static ArrayList<String> removehyperlink(ArrayList<String> s){
		String str = " ";
		
		for(int i=0; i < s.size(); i++) {		//Converts the ArrayList into a string with each element of the ArrayList separated by a Space.
			String temp = s.get(i);
			str += temp + " ";
		}
		
			//CLEAN THE STRING
			while(str.contains("</a>")) 		str = str.replaceAll("</a>"		," ");
			while(str.contains("<html>")) 		str = str.replaceAll("<html>"		," ");
			while(str.contains("<head>")) 		str = str.replaceAll("<head>"		," ");
			while(str.contains("<title>")) 		str = str.replaceAll("<title>"		," ");
			while(str.contains("<strong>")) 	str = str.replaceAll("<strong>"		," ");
			while(str.contains("<br>")) 		str = str.replaceAll("<br>"		," ");
			while(str.contains("</html>")) 		str = str.replaceAll("</html>"		," ");
			while(str.contains("</head>")) 		str = str.replaceAll("</head>"		," ");
			while(str.contains("</title>")) 	str = str.replaceAll("</title>"		," ");
			while(str.contains("</strong>")) 	str = str.replaceAll("</strong>"	," ");
			while(str.contains("<a")) 		str = str.replaceAll("<a)"		," ");
			while(str.contains("\">")) 		str = str.replaceAll("\">"		," ");
			while(str.contains("href=")) 		str = str.replaceAll("href="		," ");
			while(str.contains("\"")) 		str = str.replaceAll("\""		," ");
			while(str.contains("NOSPAM")) 		str = str.replaceAll("NOSPAM"		,"");
			
		System.out.println("After Clean: " + str);
			
		ArrayList<String> temp = new ArrayList<String>();		//Takes every element of the new String (which was the original ArrayList concatenated with 
		String[] elements = str.trim().split(" ");			//spaces between each element.  Thus this makes a String array and ArrayList out of the String
										//"temp" will now be the Original ArrayList, but without HTML formatting symbols.
			
		for(int i=0; i<elements.length; i++) {
			temp.add(elements[i]);
		}
			
		for(int i=0; i<temp.size(); i++) { for(int j = i+1; j<temp.size(); j++) { if(temp.get(i).equals(temp.get(j))) { 
			temp.remove(j);
			j--;
		}}}
		
		for(int i=0; i<temp.size(); i++) {
			if(temp.get(i).equals(" ")) 		temp.remove(i);
			if(temp.get(i).equals(".")) 		temp.remove(i);
			if(!(temp.get(i).contains("."))) 	temp.remove(i);
		}
			
		return temp;
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
		
		//Scan the whole webpage into a single readable string
		String whole = "";
		
		while(scan1.hasNext()){
			String addendum = scan1.next();
			
			if (addendum.equals("\n")) addendum = " "; 		//Cleans out line breaks with spaces
			
			whole += addendum + " ";
			}
		
		//Remove hyperlinks
		ArrayList<String> emails = new ArrayList<String>();
		
		//More robust method for getting emails in the standard form
		for(int i = 0; i < whole.length(); i++){
			if(whole.charAt(i) == '@')
			{
				int start = whole.lastIndexOf(' ', i);
				int finish = whole.indexOf(' ', i);
				
				if(whole.charAt(finish - 1) == '.')
				{
					while(!(emails.contains(whole.substring(start, finish - 1))))  //Removes the period if it is punctuation
					{
						emails.add(whole.substring(start, finish - 1));
					}
				}
				else
				{
					while(!(emails.contains(whole.substring(start, finish))))		//Leaves it otherwise
					{
						emails.add(whole.substring(start, finish));
					}
				}
			}
		}
		
		emails = EmailFinder.removehyperlink(emails);
		
		//Removes invalid addresses that contain @ but are not emails addresses
		for(int i = 0; i < emails.size(); i++){
			int c = emails.get(i).indexOf('@');
			int size = emails.get(i).length();
			
			String end = null;
			
			if (c != -1) {
				end = emails.get(i).substring(c, size);
				
				if(end.length() < 2){
					emails.remove(i);
				}
			}
			
		}
		
		
		for(int i = 0; i < emails.size(); i++){
			System.out.println("Found: " + emails.get(i));
		}

}}
