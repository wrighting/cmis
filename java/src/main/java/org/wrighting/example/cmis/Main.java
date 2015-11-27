package org.wrighting.example.cmis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class Main {

	public static Session createAtomPubSession(final String user, final String password, final String url) {
		return Main.createAtomPubSession(user, password, url, "-default-");
	}

	public static Session createAtomPubSession(final String user, final String password, final String url,
			final String repo) {
		// default factory implementation
		SessionFactory factory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// user credentials
		parameter.put(SessionParameter.USER, user);
		parameter.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameter.put(SessionParameter.ATOMPUB_URL, url);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.REPOSITORY_ID, repo);

		// create session
		Session session = factory.createSession(parameter);

		return session;
	}



	public static void main(String[] args) {

		for(int i=0;i<args.length;i++) {
			System.out.println("Arg " + i + "=" + args[i]);
		}
		Properties p = new Properties(System.getProperties());
		System.out.println("Number of args:" + args.length);
		if (args.length > 1) {
			//String propsFile = p.getProperty("config");
			String propsFile = args[0];
			System.out.println("Loading properties from:" + propsFile);
			// set up new properties object
			// from file "myProperties.txt"
			FileInputStream propFile;
			try {
				propFile = new FileInputStream(propsFile);
				p.load(propFile);
			} catch (FileNotFoundException e) {
				System.err.println("Could not open config properties file:" + propsFile);
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Could not load config properties file:" + propsFile);
				e.printStackTrace();
				System.exit(1);
			}

		} else {
			System.out.println("No config file specified");
			System.exit(1);
		}
		
		String user = p.getProperty("user");
		String password = p.getProperty("password");
		String url = p.getProperty("url");
		String path = p.getProperty("path");

		Session session = Main.createAtomPubSession(user, password, url);

		CmisObject dest = session.getObjectByPath(path);
		
		String action = args[1];
		String name = null;
		switch (action) {
		case "upload":
			//Name of file to upload
			name = args[2];
			break;
			default:
				System.err.println("Undefined action:" + action);
				System.exit(1);
		}

		try {
			Upload.uploadDocument(name, (Folder) dest);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
