// Ben Greenawald (bhg5yd) and Ben Canty (rbc6du)

//	I cleaned up a lot of the stuff and reodered things (I like main going before its methods)
//	This now works for all the simple and normal emails on the page (and one of the bonous--almost)
//	I'd reccomend just copying all of this into Eclypse, because I changed a lot of things.
//	Sorry about that.


import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

// TEST ON:   http://cs1110.cs.virginia.edu/emails.html

public class EmailFinder {


	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		
		//Gets URL from the user
		System.out.print("Please enter a URL: ");
		String url1 = scan.nextLine();
		URL url = new URL(url1);
		Scanner scan1 = new Scanner(url.openStream());
		
		//Scan the whole webpage into a single readable string
		String whole = "";
		while(scan1.hasNext()) {
			String addendum = scan1.next();
			//Cleans out line breaks with spaces
			if (addendum.equals("\n")) addendum = " ";
			
			whole += addendum + " ";
		}
		
		ArrayList<String> emails = new ArrayList<String>();
		
		whole = cleanFor( whole,  "(dot)",  ".");
		whole = cleanFor( whole,  "(at)",  "@");
		whole = cleanFor( whole,  " dot ",  ".");
		whole = cleanFor( whole,  " at ",  "@");
		
		//More robust method for getting emails in the standard form
		for(int i = 0; i < whole.length(); i++){
			if(whole.charAt(i) == '@')
			{
				int start = whole.lastIndexOf(' ', i);
				int finish = whole.indexOf(' ', i);
				
				if(whole.charAt(finish - 1) == '.') {
					//Removes the period if it is punctuation
					while(!(emails.contains(whole.substring(start, finish - 1)))) {
						emails.add(whole.substring(start, finish - 1));
					}
				} else { 	//Leaves it otherwise
					while(!(emails.contains(whole.substring(start, finish)))) {
						emails.add(whole.substring(start, finish));
					}
				}
			}
		}

		//removes elements of HTML formatting
		emails = EmailFinder.removeHTML(emails);
		
		//Removes invalid addresses that contain @ but are not emails addresses
		for(int i = 0; i < emails.size(); i++){
			int c = emails.get(i).indexOf('@');
			int size = emails.get(i).length();
			
			String end = null;
			
			/*A2Shrt*/if (c != -1) {
				end = emails.get(i).substring(c, size);
				if(end.length() < 2) {	
					System.out.println("Error_2Shrt: " + emails.get(i).toString());
					emails.remove(i);
				}
			}
			
		}
		
		// Removes cases with not a valid .whatever
		for (int i = 0; i < emails.size(); i++) {
			int size = emails.get(i).length();
			boolean delete = false;
			String end = "22";
			
			int f = emails.get(i).indexOf('@');
			//must ensure that end is not lengthless
			if(!(f+1 > size)) {end = emails.get(i).substring(f + 1, size);}
			
			//remove if @------ doesn't have a "."
			if(!(end.contains("."))) {
				delete = true;
			}
			
			//make sure f is valid
			if(f != -1) {
				//Shift the end to be the VERY LAST things after a "."
				while(end.contains(".")) {
					f = emails.get(i).indexOf('.', f + 1);
					end = emails.get(i).substring(f + 1, size);
				}
			}
			
			//extra credit:
			if(end.equalsIgnoreCase("_du")) { end = "edu";}
				
			//Destroy is less than 2 characters long
			if(end.length() < 2) { 
				delete = true;
			}
			
			//Destroy if it contains a digit or illegal symbol/number
			for(int character=0; character <= 64; character++) {
				CharSequence test = "" + (char) character;
				if ((end.contains(test))) {
					delete = true;
				}
			} 

			//Destroy if it contains a digit or illegal symbol
			for(int character=91; character <= 96; character++) {
				CharSequence test = "" + (char) character;
				if ((end.contains(test))) {
					delete = true;
				}
			}

			//Destroy if it contains a digit or illegal symbol
			for(int character=123; character <= 127; character++) {
				CharSequence test = "" + (char) character;
				if ((end.contains(test))) {
					delete = true;
				}
			}
			
			//Deletes the email is it is faulty, and goes back a step in the FOR loop so we dont' skip anything
			if (delete == true) {
				emails.remove(i);
				i--;
			}
		}
		//Remove cases with the underscore in the ending THIS DOES NOT WORK PERFECTLY YET
		//Remove cases with the underscore in the ending
		for(int k = 0; k < emails.size(); k++){
			int index = emails.get(k).indexOf('@');
			String temp = emails.get(k).substring(index + 1, emails.get(k).length());
			temp = cleanFor(temp, ".", "");
			
			for(int q = 0; q < temp.length(); q++){
				if(!(Character.isLetter(temp.charAt(q)))){
					emails.remove(k);
					k--;
					continue;
			
				}
			}
			
		}

		for(int i = 0; i < emails.size(); i++) { System.out.println("Found: <" + emails.get(i) + ">"); }

}
	
	public static String cleanFor(String subject, String err, String repl){
		while(subject.contains(err)){
			subject = subject.replace(err,repl);
		}
		return subject;
	}
	
	public static ArrayList<String> removeHTML(ArrayList<String> s){
		String str = " ";
		
		//Converts the ArrayList into a string with each element of the ArrayList separated by a Space.
		for(int i=0; i < s.size(); i++) {		
			String temp = s.get(i);
			str += temp + " ";
		}
		//
		
		//CLEAN THE STRING
		str = cleanFor(str, "</a>",		 " ");
		str = cleanFor(str, "<html>",	 " ");
		str = cleanFor(str, "<head>",	 " ");
		str = cleanFor(str, "<title>",	 " ");
		str = cleanFor(str, "<strong>",	 " ");
		str = cleanFor(str, "<br>",		 " ");
		str = cleanFor(str, "</html>",	 " ");
		str = cleanFor(str, "</head>",	 " ");
		str = cleanFor(str, "</title>",	 " ");
		str = cleanFor(str, "</strong>", " ");
		str = cleanFor(str, "<a",		 " ");
		str = cleanFor(str, "\">",		 " ");
		str = cleanFor(str, "href=",	 " ");
		str = cleanFor(str, "\"",		 " ");
		str = cleanFor(str, "NOSPAM",	  "");
		
		//Takes every element of the new String (which was the original ArrayList concatenated with 
		//spaces between each element.  Thus this makes a String array and ArrayList out of the String
		//"temp" will now be the Original ArrayList, but without HTML formatting symbols.
		ArrayList<String> temp = new ArrayList<String>();		
		String[] elements = str.trim().split(" ");				
																
			
		for(int i=0; i<elements.length; i++) temp.add(elements[i]);
			
		for(int i=0; i<temp.size(); i++) { 
			for(int j = i+1; j<temp.size(); j++) { 
				if(temp.get(i).equals(temp.get(j))) { 
					temp.remove(j);
					j--;
				}
			}
		}
		
		for(int i=0; i<temp.size(); i++) { if(temp.get(i).equals(" ")) 		{temp.remove(i);}}
		for(int i=0; i<temp.size(); i++) { if(temp.get(i).equals(".")) 		{temp.remove(i);}}
		
		for(int i=0; i<temp.size(); i++) {
			String test = temp.get(i);
			if(test.endsWith(".")) temp.set(i, test.substring(0 , test.length()-1) );
			if(test.endsWith("?")) temp.set(i, test.substring(0 , test.length()-1) );
			if(test.endsWith(",")) temp.set(i, test.substring(0 , test.length()-1) );
		}
			
		return temp;
	}

}
