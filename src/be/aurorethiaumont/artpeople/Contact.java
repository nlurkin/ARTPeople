package be.aurorethiaumont.artpeople;

import java.util.ArrayList;

public class Contact {
	Contact(String fn, String n, ArrayList<String> ph, ArrayList<String> em){
		name = n;
		first_Name = fn;
		phone = ph;
		email = em;
	}
	
	public String name;
	public String first_Name;
	public ArrayList<String> phone;
	public ArrayList<String> email;
}
