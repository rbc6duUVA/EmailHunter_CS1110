// Ben Greenawald (bhg5yd) and Ben Canty (rbc6du)

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// EmailFinder.java																												//
// 																																//
// CS 1110 Project: EmailFinder																									//
// Authors: Ben Canty and Ben Greenawald																						//
// Date Started: 	Friday,17 October 2014																						//
// Date Completed: 	Monday, 27 October 2014																						//
// Date Due: 		Wednesday, 29 October 2014																					//
// 																																//
// Design a program that will receive a URL and mine it for email addresses.													//
// It should work for any type of email (ideally)																				//
// 																																//
// This design works for any "String1"@"String2"."String3"																		//
// Where:																														//
// 		String1 is anything																										//
// 		String2 is anything																										//
// 		String3 is >=2 characters long and contains no numbers																	//
// 																																//
// This design works for emails displayed in plain text, &#nnnn, &#xhhhh, and reversed text formats (Low Fidelity).				//
// This design attempts to repair emails that have undergone "_" substitutions.													//
// This design also tries to correct for anti-email-mining methods:																//
//  	Awkward spacing																											//
//  	Character substitutions ( "at" for "@" , " (dot) " for "." )															//
//  	Null insertions (myemail@virgiNOSPAMnia.edu)																			//
//  																															//
//  Tests indicate that this program is running well.																			//
//  Tests run on url: http://cs1110.cs.virginia.edu/emails.html																	//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class EmailFinder {

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		ArrayList<String> emails = new ArrayList<String>();
		
		//Gets URL from the user
		System.out.print("What web page should we search for email addresses? ");
		String url1 = scan.nextLine();
		URL url = new URL(url1);
		Scanner scan1 = new Scanner(url.openStream());
		
		//Scan the whole webpage into a single readable string
		String whole = "";
		while(scan1.hasNext()) {
			String addendum = scan1.next();
			//Cleans out line breaks with spaces
			if (addendum.equals("\n")) {addendum = " ";}
			whole += addendum + " ";
		}
		
//////////Conditions "whole" for easier reading///////////////////////////////////////////////////////////////////////////////////
		//Makes the String easy to read for emails, and easy to make corrections
		whole = whole.toLowerCase();
		
		whole = cleanFor( whole,  "(dot)",  ".");
		whole = cleanFor( whole,  "(at)",  "@");
		whole = cleanFor( whole,  " .",  ".");
		//whole = cleanFor( whole,  ". ",  ".");  Causes error with emails at the end of sentences.
		whole = cleanFor( whole,  " @",  "@");
		whole = cleanFor( whole,  "@ ",  "@");
		
		whole = cleanFor( whole,  " dot ",  ".");
		whole = cleanFor( whole,  " at ",  "@");
		
		whole = cleanFor( whole,  ". edu",  ".edu");
		whole = cleanFor( whole,  ". com",  ".com");
		whole = cleanFor( whole,  ". net",  ".net");
		
		//Will convert alternate character forms into readable characters.
		whole = cleanCharHTML(whole);
		
		//Used for formatting issue specific to "cleanCharHTML"
		//but may also apply to other emails introduced "send and email to me:email@address.com"
		whole = cleanFor( whole,  ":",  " ");
		
//////////Creates and ArrayList<String> of all potential emails///////////////////////////////////////////////////////////////////
		//More robust method for getting emails in the standard form
		for(int i = 0; i < whole.length(); i++){
			if(whole.charAt(i) == '@')
			{
				int start = whole.lastIndexOf(' ', i);
				int finish = whole.indexOf(' ', i);
				
				//Removes the period if it is punctuation
				if(whole.charAt(finish - 1) == '.') {
					while(!(emails.contains(whole.substring(start, finish - 1)))) {
						emails.add(whole.substring(start, finish - 1));
					}
				} 
				//Leaves it otherwise
				else {
					while(!(emails.contains(whole.substring(start, finish)))) {
						emails.add(whole.substring(start, finish));
					}
				}
			}
		}

//////////Append hidden potentail emails//////////////////////////////////////////////////////////////////////////////////////////
		//removes elements of HTML formatting
		emails = EmailFinder.removeHTML(emails);
		//Provides for the possibility of reversed emails. (moc.liamg@neb)
		emails = reverse(emails);
		
		//Removes invalid addresses that contain @ but are not emails addresses
		for(int i = 0; i < emails.size(); i++){
			int c = emails.get(i).indexOf('@');
			int size = emails.get(i).length();
			
			String end = null;
			
			if (c != -1) {
				end = emails.get(i).substring(c, size);
				if(end.length() < 2) {	
					emails.remove(i);
				}
			}
			
		}
		
//////////Tests for validity and Retains only valid email addresses///////////////////////////////////////////////////////////////
		// Removes invalid emails (attempts a repair first
		for (int i = 0; i < emails.size(); i++) {
			int size = emails.get(i).length();
			boolean delete = false;
			String end = "23";
			
			int f = emails.get(i).indexOf('@');
			//must ensure that end is not lengthless
			if(!(f+1 > size)) {
				end = emails.get(i).substring(f + 1, size);
			}
			
			//High fidelity email repair
			emails = repairEmail(emails, i, end);
			end = emails.get(i).substring(f + 1, size);
			
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
					//Low fidelity email repair, with end redefinition.
					emails = repairEmail(emails, i, end);
					end = emails.get(i).substring(f + 1, size);
				}
			}
			
			//make sure f is valid
			if(f != -1) {
				//Shift the end to be the VERY LAST things after a "."
				while(end.contains(".")) {
					f = emails.get(i).indexOf('.', f + 1);
					end = emails.get(i).substring(f + 1, size);
				}
			}

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

//////////Prints out all mined emails from webpage////////////////////////////////////////////////////////////////////////////////
		for(int i = 0; i < emails.size(); i++) { System.out.println("Found: <" + emails.get(i) + ">"); }
		scan.close();
		scan1.close();
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////  Methods Used in Program  ///////////////////////////////////////////////////////////////////////////////////////////////////

	/**Takes a String and finds "err" and replaces it with "repl" for every instance.
	 * @param subject
	 * @param err
	 * @param repl
	 * @return cleaned version of the String.
	 */
	public static String cleanFor(String subject, String err, String repl){
		while(subject.contains(err)){
			subject = subject.replace(err,repl);
		}
		return subject;
	}
	
	
	
	/**Attempts to repair emails that have every instance of a letter substituted by an underscore.
	 * @param als
	 * @param index
	 * @param dmgString
	 * @return an ArrayList<String> with all repaired elements.
	 */
	public static ArrayList<String> repairEmail(ArrayList<String> als, int index, String dmgString) {
		String repl = "";
		
		if(dmgString.contains(" ")) dmgString = cleanFor(dmgString," ","");
		
		//High Fidelity
		//Run after END is defined, but before refined
		if(dmgString.equalsIgnoreCase("_irginia.edu")) repl = "v";
		if(dmgString.equalsIgnoreCase("v_rg_n_a.edu")) repl = "i";
		if(dmgString.equalsIgnoreCase("vi_ginia.edu")) repl = "r";
		if(dmgString.equalsIgnoreCase("vir_inia.edu")) repl = "g";
		if(dmgString.equalsIgnoreCase("virgi_ia.edu")) repl = "n";
		if(dmgString.equalsIgnoreCase("virgini_.edu")) repl = "a";
		if(dmgString.equalsIgnoreCase("virginia._du")) repl = "e";
		if(dmgString.equalsIgnoreCase("virginia.e_u")) repl = "d";
		if(dmgString.equalsIgnoreCase("virginia.ed_")) repl = "u";
		
		if(dmgString.equalsIgnoreCase("_mail.com")) repl = "g";
		if(dmgString.equalsIgnoreCase("g_ail.co_")) repl = "m";
		if(dmgString.equalsIgnoreCase("gm_il.com")) repl = "a";
		if(dmgString.equalsIgnoreCase("gma_l.com")) repl = "i";
		if(dmgString.equalsIgnoreCase("gmai_.com")) repl = "l";
		if(dmgString.equalsIgnoreCase("gmail._om")) repl = "c";
		if(dmgString.equalsIgnoreCase("gmail.c_m")) repl = "o";
		
		if(dmgString.equalsIgnoreCase("_ahoo.com")) repl = "y";
		if(dmgString.equalsIgnoreCase("y_hoo.com")) repl = "a";
		if(dmgString.equalsIgnoreCase("ya_oo.com")) repl = "h";
		if(dmgString.equalsIgnoreCase("yah__.c_m")) repl = "o";
		if(dmgString.equalsIgnoreCase("yahoo._om")) repl = "c";
		if(dmgString.equalsIgnoreCase("yahoo.co_")) repl = "m";
		
		if(dmgString.equalsIgnoreCase("_om_ast.net")) repl = "c";
		if(dmgString.equalsIgnoreCase("c_mcast.net")) repl = "o";
		if(dmgString.equalsIgnoreCase("co_cast.net")) repl = "m";
		if(dmgString.equalsIgnoreCase("comc_st.net")) repl = "a";
		if(dmgString.equalsIgnoreCase("comca_t.net")) repl = "s";
		if(dmgString.equalsIgnoreCase("comcas_.ne_")) repl = "t";
		if(dmgString.equalsIgnoreCase("comcast._et")) repl = "n";
		if(dmgString.equalsIgnoreCase("comcast.n_t")) repl = "e";
		
		if(dmgString.equalsIgnoreCase("_ellsouth.net")) repl = "b";
		if(dmgString.equalsIgnoreCase("b_llsouth.net")) repl = "e";
		if(dmgString.equalsIgnoreCase("be__south.net")) repl = "l";
		if(dmgString.equalsIgnoreCase("bell_outh.net")) repl = "s";
		if(dmgString.equalsIgnoreCase("bells_uth.net")) repl = "o";
		if(dmgString.equalsIgnoreCase("bellso_th.net")) repl = "u";
		if(dmgString.equalsIgnoreCase("bellsou_h.ne_")) repl = "t";
		if(dmgString.equalsIgnoreCase("bellsout_.net")) repl = "h";
		if(dmgString.equalsIgnoreCase("bellsouth._et")) repl = "n";
		if(dmgString.equalsIgnoreCase("bellsouth.n_t")) repl = "e";
		
		if(dmgString.equalsIgnoreCase("_ol.com")) repl = "a";
		if(dmgString.equalsIgnoreCase("a_l.c_m")) repl = "o";
		if(dmgString.equalsIgnoreCase("ao_.com")) repl = "l";
		if(dmgString.equalsIgnoreCase("aol._om")) repl = "c";
		if(dmgString.equalsIgnoreCase("aol.co_")) repl = "m";
		
		if(dmgString.equalsIgnoreCase("_otmail.com")) repl = "h";
		if(dmgString.equalsIgnoreCase("h_tmail.c_m")) repl = "o";
		if(dmgString.equalsIgnoreCase("ho_mail.com")) repl = "t";
		if(dmgString.equalsIgnoreCase("hot_ail.co_")) repl = "m";
		if(dmgString.equalsIgnoreCase("hotm_il.com")) repl = "a";
		if(dmgString.equalsIgnoreCase("hotma_l.com")) repl = "i";
		if(dmgString.equalsIgnoreCase("hotmai_.com")) repl = "l";
		if(dmgString.equalsIgnoreCase("hotmail._om")) repl = "c";
		
		//Low Fidelity
		//Run after END is refined
		if(dmgString.equalsIgnoreCase("_du")) repl = "e";
		if(dmgString.equalsIgnoreCase("e_u")) repl = "d";
		if(dmgString.equalsIgnoreCase("ed_")) repl = "u";
		
		if(dmgString.equalsIgnoreCase("_om")) repl = "c";
		if(dmgString.equalsIgnoreCase("c_m")) repl = "o";
		if(dmgString.equalsIgnoreCase("co_")) repl = "m";
		
		if(dmgString.equalsIgnoreCase("_et")) repl = "n";
		if(dmgString.equalsIgnoreCase("n_t")) repl = "e";
		if(dmgString.equalsIgnoreCase("ne_")) repl = "t";
		
		//Cleans the entire email address
		if(!(repl.isEmpty())) {
			String temp = als.get(index);
			temp = cleanFor(temp,"_",repl);
			temp = cleanFor(temp," ","");
			als.set(index, temp);
		}
		
		return als;
	}
	
	
	
	/**Removes remnants of HTML formatting (except for alternate character systems)
	 * ie. <a> <strong> </head>
	 * @param s
	 * @return an ArrayList<String> with all cleaned elements.
	 */
	public static ArrayList<String> removeHTML(ArrayList<String> s){
		String str = " ";
		
		//Converts the ArrayList into a string with each element of the ArrayList separated by a Space.
		for(int i=0; i < s.size(); i++) {		
			String temp = s.get(i);
			str += temp + " ";
		}
		
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
		str = cleanFor(str, "nospam",	  "");
		
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
	
	
	
	/**Converts <#&nnnn> and <#&hhhh> to readable characters (in string format).
	 * @param s
	 * @return a string
	 */
	public static String cleanCharHTML (String s) {
		String temp = s;
		
		//Converts all "&#xhhhh;" to "&#nnnn;"
		HTMLLoop:
		while(temp.contains("&#")) {
			int start = temp.indexOf("&#x");
			int end = temp.indexOf(";", start);

			if (start == -1 || end == -1) {
				break HTMLLoop;
			}
			
			temp = cleanFor(
					temp,
					"&#x" + temp.substring(start+3, end) + ";",
					"&#" + Integer.parseInt(temp.substring(start+3, end), 16) + ";"
			);
		}
		
		//Converts all "&#nnnn;" to "character"
		for(int num=32; num <= 126; num++) {
			temp = cleanFor(temp, "&#"+num+";", "" + (char)num);
		}
		
		//Return the cleaned string
		return temp;
	}

	
	
	/**Appends to the ArrayList a new set of strings that are potentially reversed emails.
	 * This must be done after the ArrayList is constructed and defined but before it is filtered.
	 * @param e
	 * @return ArrayList with additional elements
	 */
	public static ArrayList<String> reverse(ArrayList<String> e){
		/*
		 *This is a reverse method, it should take in the arraylist of strings, which at this point should still
		 *contain the reversed strings, identify and potential candidates, and reverse the string. To identify basic candidates, 
		 *it should find any string that has an @ sign (all of them should have at signs at this point) and a period before the at sign but not after (this guarantees
		 *that no valid emails are changed, but it does make it possible to miss a few reverse ones). It then takes the strings. and using a temp string and a for loop,
		 *reverses the strings before adding them to the arraylist (no need to remove the old ones becomes later trials should remove them. Then these candidates will
		 *be subject to the later trials and should they pass, they are potentially valid emails.
		 */
		for(int i = 0; i < e.size(); i++){
			String temp = e.get(i);
			int at = temp.indexOf('@');
			//Creates a before at substring and an after at substring
			//Run tests on both of these substrings to check for potential reverse emails
			//If the end doesn't have a period but the start does, it is a good candidate for a reverse email
			//If both have a period, we are unsure, but we do not want to risk reversing an email that is in the correct order
			String start = temp.substring(0);
			String end = temp.substring(at + 1);
			if(start.contains(".") && !(end.contains("."))){
				String reverse = "";
				for(int j = e.get(i).length() - 1; j >= 0; j--){
					reverse += e.get(i).charAt(j);
				}
				e.add(reverse);
			}
		}
		
		return e;
	}
	
//FIN
}
