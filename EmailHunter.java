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
		
			//CLEAN THE STRING 			".*(jim|joe).*"
			while(str.contains("<html>")) 		str.replace(Pattern.quote("<html>"),"");
			while(str.contains("<head>")) 		str.replace(Pattern.quote("<head>"),"");
			while(str.contains("<title>")) 		str.replace(Pattern.quote("<title>"),"");
			while(str.contains("<strong>")) 	str.replace(Pattern.quote("<strong>"),"");
			while(str.contains("<br>")) 		str.replace(Pattern.quote("<br>)"),"");
			while(str.contains("<a")) 			str.replace(Pattern.quote("<a)"),"");
			while(str.contains("\">")) 			str.replace(Pattern.quote("\">"),"");
			while(str.contains("</html>")) 		str.replace(Pattern.quote("</html>"),"");
			while(str.contains("</head>")) 		str.replace(Pattern.quote("</head>"),"");
			while(str.contains("</title>")) 	str.replace(Pattern.quote("</title>"),"");
			while(str.contains("</strong>")) 	str.replace(Pattern.quote("</strong>"),"");
			while(str.contains("</a>")) 		str.replace(Pattern.quote("</a>"),"");
			while(str.contains("href=\"")) 		str.replace(Pattern.quote("href=\""),"");

			
		System.out.println("After Clean: " + str);
			
		ArrayList<String> temp = new ArrayList<String>();		//Takes every element of the new String (which was the original ArrayList concatenated with 
		String[] elements = str.trim().split(" ");				//spaces between each element.  Thus this makes a String array and ArrayList out of the String
																	//"temp" will now be the Original ArrayList, but without HTML formatting symbols.
			
		for(int i=0; i<elements.length; i++) {
			temp.add(elements[i]);
		}
			
		System.out.println("DONE: removing Hyperlinks!");
		System.out.println("POST: " + temp);
			
		return temp;
		/*
		for(int i = 0; i < s.size(); i++){			//So this removes the "<br>" component of a hyperlink?
			while(s.get(i).contains("<br>")){
				s.remove("<br>");
				//String temp = s.get(i);
				//s.remove(i);
				//temp = temp.replace("<br>", "");
				//s.add(temp);
			}
		}
		for(int i = 0; i < s.size(); i++){
			while(s.get(i).contains("</a>")){
				s.remove("</a>");
				//String temp = s.get(i);
				//s.remove(i);
				//temp = temp.replace("</a>", "");
				//s.add(temp);
				
			}
		}
		for(int i = 0; i < s.size(); i++){ //THIS IS BUGGY AND DOES NOT WORK YET
			while(s.get(i).contains("href=")){
				s.remove("href=");
				
				//int c = s.get(i).indexOf('@');
				//int start = s.get(i).lastIndexOf('"', c);
				//int end = s.get(i).indexOf('"',c);
				//String temp = s.get(i).substring(start + 1, end);
				//s.remove(i);
				//while(!(s.contains(temp)))
				//	s.add(temp);
			
			}
		}
		
		return s;
		*/
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
