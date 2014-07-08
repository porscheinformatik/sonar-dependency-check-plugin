/*
 * Sonar Dependency Check Plugin
 * Copyright (C) 2013 Porsche Informatik
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.dependencycheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbXmlHandler {

	// Export: Marshalling
    public static void marshal(DependencyWrapper list, File selectedFile)
            throws IOException, JAXBException {
        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(selectedFile));
        context = JAXBContext.newInstance(DependencyWrapper.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(list, writer);
        writer.close();
    }
    
    public static void marshal(LicenseWrapper list, File selectedFile) throws IOException, JAXBException {
        // TODO Auto-generated method stub
        
        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(selectedFile));
        context = JAXBContext.newInstance(LicenseWrapper.class/*ProjectDependency.class*/);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(list, writer);
        writer.close();
    }
 
    // Import: Unmarshalling
    public static DependencyWrapper unmarshalAllowedProjectDependencies(StringReader stringReader) throws JAXBException {

    	DependencyWrapper allowedPds= null;
        JAXBContext context;
 
        context = JAXBContext.newInstance(DependencyWrapper.class);
        Unmarshaller um = context.createUnmarshaller();
        allowedPds= (DependencyWrapper) um.unmarshal(stringReader);
 
        return allowedPds;
    }
	
    public static LicenseWrapper unmarshalLicenseWrapper(StringReader stringReader) throws JAXBException {
        
        LicenseWrapper licenses= null;
        JAXBContext context;
        
        context= JAXBContext.newInstance(LicenseWrapper.class);
        Unmarshaller um= context.createUnmarshaller();
        licenses=(LicenseWrapper) um.unmarshal(stringReader);
        
        return licenses;
    }
    
}
