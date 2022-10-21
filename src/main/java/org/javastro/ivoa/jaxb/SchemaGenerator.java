/*
 * $Id$
 * 
 * Created on 26 Oct 2013 by Paul Harrison (paul.harrison@manchester.ac.uk)
 * Copyright 2013 Manchester University. All rights reserved.
 *
 * This software is published under the terms of the Academic 
 * Free License, a copy of which has been included 
 * with this distribution in the LICENSE.txt file.  
 *
 */

package org.javastro.ivoa.jaxb;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;


/**
 * Generate schema from the JAXB models.
 * 
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 26 Oct 2013
 * @version $Revision$ $date$
 */
public class SchemaGenerator {

    
    public void generate(String context) throws JAXBException, IOException
    {
        generate(context,new SchemaNamer());
    }
   
    public void generate(String contextPath,SchemaOutputResolver sor) throws JAXBException, IOException {
        javax.xml.bind.JAXBContext jaxbContext = JAXBContext
                .newInstance(contextPath);
        System.out.println("generating schema for regtap");
        jaxbContext.generateSchema(sor);
    }

    public static void main(String[] args) {
        SchemaGenerator sg = new SchemaGenerator();
        try {
            sg.generate(args[0]);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

/*
 * $Log$
 */
