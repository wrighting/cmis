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



	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			System.out.println("Arg " + i + "=" + args[i]);
		}
		Properties p = new Properties(System.getProperties());
		System.out.println("Number of args:" + args.length);
		if (args.length > 1) {
			// String propsFile = p.getProperty("config");
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

		Session session = CmisSession.createAtomPubSession(user, password, url);

		CmisObject dest = session.getObjectByPath(path);

		String action = args[1];

		switch (action) {
		case "upload": {
			// Name of file to upload
			String name = args[2];
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
			break;
		case "list": {
			String filepath = args[2];
			Extension.listExtensions(session, filepath);
		}
			break;
		case "set-property": {
			String filepath = args[2];
			String name = args[3];
			String value = args[4];
			Extension.setExtensionValue(session, filepath, name, value);
		}
			break;
		default:
			System.err.println("Undefined action:" + action);
			// System.exit(1);
		}

	}

}
