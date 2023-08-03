/*
 * Created on 5 Nov 2021 
 * Copyright 2021 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.javastro.ivoa.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.util.ValidationEventCollector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.javastro.ivoa.jaxb.DescriptionValidator;
import org.javastro.ivoa.jaxb.JaxbAnnotationMeta;

/**
 *  .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 5 Nov 2021
 */
public abstract class AbstractJAXBJPATest {


    protected jakarta.persistence.EntityManager setupDB(String puname) {
            Map<String, String> props = new HashMap<>();
          
          //
            
          //derby
          props.put("jakarta.persistence.jdbc.url", "jdbc:derby:memory:"+puname+";create=true");//IMPL differenrt DB for each PU to stop interactions
    //        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:derby:emerlindb;create=true;traceFile=derbytrace.out;traceLevel=-1;traceDirectory=/tmp");
          props.put("jakarta.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
          // props.put(PersistenceUnitProperties.TARGET_DATABASE, "org.eclipse.persistence.platform.database.DerbyPlatform");
    
    //        //h2
    //        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:mem:"+puname+";DB_CLOSE_DELAY=-1");//IMPL differenrt DB for each PU to stop interactions
    //        props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
    //        props.put(PersistenceUnitProperties.TARGET_DATABASE, "org.eclipse.persistence.platform.database.H2Platform");
    //        
    //        //hsqldb
    //        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:hsqldb:mem:"+puname+";");//IMPL differenrt DB for each PU to stop interactions
    //        props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.hsqldb.jdbcDriver");
    //        props.put(PersistenceUnitProperties.TARGET_DATABASE, "org.eclipse.persistence.platform.database.HSQLPlatform");
          
          
          // props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_BOTH_GENERATION);
          props.put("jakarta.persistence.schema-generation.scripts.create-target", "test.sql");
          props.put("jakarta.persistence.schema-generation.scripts.drop-target", "test-drop.sql");
          props.put("hibernate.hbm2ddl.schema-generation.script.append", "false");
          
          props.put("jakarta.persistence.schema-generation.create-source", "metadata");
          props.put("jakarta.persistence.schema-generation.database.action", "drop-and-create");
          props.put("jakarta.persistence.schema-generation.scripts.action", "drop-and-create");
          props.put("jakarta.persistence.jdbc.user", "");
    //        props.put(PersistenceUnitProperties.CACHE_SHARED_, "false");
          
        // Configure logging. FINE ensures all SQL is shown
          //props.put(PersistenceUnitProperties.LOGGING_LEVEL, "FINEST");
           
     
          jakarta.persistence.EntityManagerFactory emf = jakarta.persistence.Persistence.createEntityManagerFactory(puname, props);
          
          jakarta.persistence.EntityManager em = emf.createEntityManager();
            return em;
        }

    protected <T> T roundtripXML(JAXBContext jc, T model, Class<T> clazz) throws ParserConfigurationException, JAXBException,
            PropertyException, TransformerFactoryConfigurationError,
            TransformerConfigurationException, TransformerException {
                StringWriter sw = new StringWriter();
                Marshaller m = jc.createMarshaller();
                
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.marshal(model, sw);
                // Actually pretty Print - as the above formatting instruction does not seem to work
                // Set up the output transformer
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer trans = transfac.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                trans.setOutputProperty(OutputKeys.INDENT, "yes"); 
            
                StringWriter sw2 = new StringWriter();
                StreamResult result = new StreamResult(sw2);
                
                trans.transform(new StreamSource(new StringReader(sw.toString())), result);
                System.out.println(sw2.toString());
            
                //try to read in again
                Unmarshaller um = jc.createUnmarshaller();
                ValidationEventCollector vc = new jakarta.xml.bind.util.ValidationEventCollector();
                um.setEventHandler(vc);
                JAXBElement<T> el = um.unmarshal(new StreamSource(new StringReader(sw2.toString())),clazz);
                if (vc.hasEvents()) {
                    for (ValidationEvent err : vc.getEvents()) {
                        System.err.println(err.getMessage());
                    }
                }
                assertTrue(!vc.hasEvents(), "reading xml back had errors");
                T modelin = el.getValue();
                assertNotNull(modelin);
                return modelin;
            }

     <T> void validate (T p,  JAXBContext jc) throws JAXBException {
       @SuppressWarnings("unchecked")
        JaxbAnnotationMeta<T> meta = JaxbAnnotationMeta.of((Class<T>)p.getClass());
        DescriptionValidator<T> validator = new DescriptionValidator<>(jc, meta);
        DescriptionValidator.Validation validation = validator.validate(p);
        if(!validation.valid) {
            System.err.println(validation.message);
        }
        assertTrue(validation.valid);
   }
}


