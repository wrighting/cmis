package org.wrighting.example.cmis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

public class Upload {
	public static Document uploadDocument(final String name, final Folder parent) throws IOException {

		File f = new File(name);
		BigInteger contentLength = BigInteger.valueOf(f.length());

		Path inputPath = Paths.get(name);
		InputStream stream = new FileInputStream(f);
		String filename = inputPath.getFileName().toString();
		String mimeType = Files.probeContentType(inputPath);
		// properties
		// (minimal set: name and object type id)
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, filename);

		ContentStream contentStream = new ContentStreamImpl(name, contentLength, mimeType, stream);

		System.out.println("Uploading:" + name + " to " + parent.getPath());
		// create a major version
		Document newDoc = parent.createDocument(properties, contentStream, VersioningState.MAJOR);
		System.out.println("Done");
		return newDoc;
	}
}
