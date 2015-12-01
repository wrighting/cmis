package org.wrighting.example.cmis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extension {

	private final static String ALFRESCO_EXTENSION_NAMESPACE = "http://www.alfresco.org";
	private final static String SET_ASPECTS = "setAspects";
	private final static String PROPERTIES = "properties";
	private final static String VALUE = "value";
	private final static String PROPERTY_DEFINITION_ID="propertyDefinitionId";
	private final static String PROPERTY_STRING="propertyString";
	
	private static Logger logger = LoggerFactory.getLogger(Extension.class);


	protected void processAttributes(CmisExtensionElement ext) {
		Map<String, String> attributes = ext.getAttributes();
		for (Entry<String, String> entry : attributes.entrySet()) {
			if (logger.isInfoEnabled()) {
	        	logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}
		}
	}
	
	public void processExtension(List<CmisExtensionElement> extensions) {
		for(CmisExtensionElement ext: extensions) {
			if (!ext.getChildren().isEmpty()) {
				if (logger.isInfoEnabled()) {
		        	logger.info("List:" + ext.getName() + ": " + ext.getValue());
				}
				this.processAttributes(ext);
				this.processExtension(ext.getChildren());
			} else {
				if (logger.isInfoEnabled()) {
		        	logger.info(ext.getName() + ": " + ext.getValue());
				}
				this.processAttributes(ext);
			}
		}
	}

	//https://forums.alfresco.com/forum/developer-discussions/alfresco-api/setting-aspect-property-using-cmis-06122014-1046
	public static void setExtensionValue(Session session, final String path, final String name, final String value) {

		CmisObject object = session.getObjectByPath(path);
		
		Properties properties = new PropertiesImpl();
        List<CmisExtensionElement> extensions = new ArrayList<CmisExtensionElement>();
 
        CmisExtensionElement valueElem = new CmisExtensionElementImpl(ALFRESCO_EXTENSION_NAMESPACE, VALUE, null, value); 
        List<CmisExtensionElement> valueElems = new ArrayList<CmisExtensionElement>();
        valueElems.add(valueElem);
 
        List<CmisExtensionElement> children = new ArrayList<CmisExtensionElement>();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(PROPERTY_DEFINITION_ID, name);
        children.add(new CmisExtensionElementImpl(ALFRESCO_EXTENSION_NAMESPACE, PROPERTY_STRING, attributes, valueElems));
 
 
        List<CmisExtensionElement> propertyValuesExtension = new ArrayList<CmisExtensionElement>();
        propertyValuesExtension.add(new CmisExtensionElementImpl(ALFRESCO_EXTENSION_NAMESPACE, PROPERTIES, null, children));
 
 
        CmisExtensionElement setAspectsExtension = new CmisExtensionElementImpl(ALFRESCO_EXTENSION_NAMESPACE, SET_ASPECTS, null, propertyValuesExtension);
        extensions.add(setAspectsExtension);
        
        if (logger.isInfoEnabled()) {
        	logger.info(extensions.toString());
        }
        properties.setExtensions(extensions);
        String repId = session.getRepositoryInfo().getId();
        Holder<String> objectIdHolder = new Holder<String>(object.getId());
        session.getBinding().getObjectService().updateProperties(repId, objectIdHolder, null, properties, null);
	}
	
	public static void listExtensions(Session session, final String path){
		CmisObject object = session.getObjectByPath(path);
		// extensions can be attached to different levels
		// in this example we get the extensions on the properties level
		List<CmisExtensionElement> extensions = object.getExtensions(ExtensionLevel.PROPERTIES);

		if(extensions == null) {
		   // this object has no extensions on this level
		   return;
		}
		Extension processor = new Extension();
		processor.processExtension(extensions);
		
	}
}
