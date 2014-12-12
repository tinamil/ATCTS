package john.pavlik;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ATC_XML_Parser extends DefaultHandler {

	StringBuffer textBuffer;
	UserFactory userFactory = new UserFactory();
	private DateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

	public static User[] Parse(File file) {
		// Use an instance of ourselves as the SAX event handler
		ATC_XML_Parser handler = new ATC_XML_Parser();
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);
			return handler.userFactory.getUsers();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public void startDocument() throws SAXException {

	}

	public void endDocument() throws SAXException {

	}

	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware
		if ("Row".equals(eName)) {
			userFactory.newUser();
		}
		if ("Table".equals(eName)) {
			userFactory.startList();
		}
	}

	public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware
		if ("Data".equals(eName)) {
			userFactory.nextString(echoText());
		}
		if ("Row".equals(eName)) {
			userFactory.finishUser();
		}
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		String s = new String(buf, offset, len);
		if (textBuffer == null) {
			textBuffer = new StringBuffer(s);
		} else {
			textBuffer.append(s);
		}
	}

	private String echoText() throws SAXException {
		if (textBuffer == null)
			return "";
		String s = "" + textBuffer;
		textBuffer = null;
		return s.trim();
	}

	private class UserFactory {
		List<String> columns = new ArrayList<>();
		String name, email, enterpriseEmail, edipi, unit;
		Boolean aup, awareness;
		Date lastCyber, aupSigned;
		ArrayList<User> list;
		boolean firstRow = false;
		int currentColumn = 0;

		private void startList() {
			list = new ArrayList<>();
			firstRow = true;
		}

		public User[] getUsers() {
			return list.toArray(new User[0]);
		}

		private void newUser() {
			name = email = enterpriseEmail = edipi = null;
			aup = awareness = null;
			lastCyber = null;
			aupSigned = null;
		}

		private void nextString(String next) {
			if (firstRow) {
				columns.add(next);
				System.out.println(next);
				return;
			}

			if (columns.size() == 0)
				return;

			String column = columns.get(currentColumn);
			currentColumn += 1;
			if (currentColumn >= columns.size())
				currentColumn = 0;

			if ("Name".equals(column))
				name = next;
			if ("AUP Uploaded".equals(column))
				aup = "Yes".equals(next);
			if ("Awareness Trained".equals(column))
				awareness = "Yes".equals(next);
			if ("E-mail".equals(column))
				email = next;
			if ("Enterprise Email".equals(column))
				enterpriseEmail = next;
			if ("EDIPI".equals(column))
				edipi = next;
			if ("Last DoD Cyber Awareness Challenge Training".equals(column)
					&& !"".equals(next))
				try {
					lastCyber = dateFormatter.parse(next);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			if ("Date AUP Signed".equals(column) && !"".equals(next))
				try {
					aupSigned = dateFormatter.parse(next);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			if ("HQ Alignment Subunit".equals(column))
				unit = next;
		}

		private void finishUser() {
			if (firstRow) {
				firstRow = false;
				return;
			}
			User user = new User("", name, email, enterpriseEmail, edipi, true,
					aup, awareness, lastCyber, aupSigned, unit);
			list.add(user);
		}
	}

}
