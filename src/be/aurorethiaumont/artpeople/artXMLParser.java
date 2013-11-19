package be.aurorethiaumont.artpeople;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class artXMLParser {
	private static final String ns = null;

	public List<Contact> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			if(in != null){
				in.close();
			}
		}
	}

	private List<Contact> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Contact> entries = new ArrayList<Contact>();
		
		parser.require(XmlPullParser.START_TAG, ns, "ContactList");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("Contact")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}  
		return entries;
	}

	private Contact readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "Contact");
		String name = null;
		String first_name = null;
		ArrayList<String> phone = new ArrayList<String>();
		ArrayList<String> email = new ArrayList<String>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag.equals("Name")) {
				name = readTextTag(parser, "Name");
			} else if (tag.equals("FirstName")) {
				first_name = readTextTag(parser, "FirstName");
			} else if (tag.equals("Phone")) {
				phone.add(readTextTag(parser, "Phone"));
			} else if (tag.equals("Email")) {
				email.add(readTextTag(parser, "Email"));
			} else {
				skip(parser);
			}
		}
		return new Contact(first_name, name, phone, email);
	}

	private String readTextTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
		String result = "";

		parser.require(XmlPullParser.START_TAG, ns, tag);
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
