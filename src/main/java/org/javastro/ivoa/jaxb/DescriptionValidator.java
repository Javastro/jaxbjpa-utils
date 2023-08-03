/*
 * $Id: DescriptionValidator.java,v 1.2 2011-09-13 13:43:32 pah Exp $
 * 
 * Created on 18 Mar 2008 by Paul Harrison (paul.harrison@manchester.ac.uk)
 * Copyright 2008 Astrogrid. All rights reserved.
 *
 * This software is published under the terms of the Astrogrid 
 * Software License, a copy of which has been included 
 * with this distribution in the LICENSE.txt file.  
 *
 */

package org.javastro.ivoa.jaxb;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.util.ValidationEventCollector;
import javax.xml.namespace.QName;

/**
 * Utility class to assist with validating the jaxb based descriptions.
 * 
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 18 Mar 2008
 * @version $Name: not supported by cvs2svn $
 * @since VOTech Stage 7
 */
public class DescriptionValidator<T> {
    /**
     * Logger for this class
     */
    /** logger for this class */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(DescriptionValidator.class);
    private final JAXBContext jc ;
    
    private final EntityMetadata<T> meta;

    
    public DescriptionValidator(JAXBContext jc, EntityMetadata<T> meta) {
        this.jc = jc;
        this.meta = meta;
    }

    public static class Validation {
        public boolean valid;
        public String message;

        public Validation(boolean v, String m) {
            valid = v;
            message = m;
        }
    }

    /**
     * Validates a specifically typed object. Note that the object does not have to 
     * have an XMLElement annotation (which is why we go to this trouble)
     * 
     * @param appdesc  a java object that has suitable jaxb annotations
     *           
     * @return
     */
    public Validation validate(final T appdesc) {
        JAXBElement<T> jaxobj = new JAXBElement<T>(
                new QName(meta.getNamespace(), meta.getName()),
                meta.getJavaType(), appdesc);
        return validate2(jaxobj);
    }

    /**
     * Validates a generic object.
     * 
     * @param appdesc  a java object that has suitable jaxb annotations
     *           
     * 
     * @return
     */
    private  Validation validate2(final Object appdesc) {
        String name = appdesc.getClass().getCanonicalName();
        try {
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ValidationEventCollector handler = new ValidationEventCollector();
            m.setEventHandler(handler);
            StringWriter sw = new StringWriter();
            m.marshal(appdesc, sw);
            StringBuffer message = new StringBuffer();
            if (handler.hasEvents()) {
                logger.error("invalid - " + name);
                for (int i = 0; i < handler.getEvents().length; i++) {
                    ValidationEvent array_element = handler.getEvents()[i];
                    logger.error(
                            "validation error - " + array_element.toString());
                    message.append(array_element.toString());
                    message.append("\n");
                }
                logger.debug(sw.toString());
            }

            return new Validation(!handler.hasEvents(), message.toString());
        } catch (JAXBException e) {
            logger.error("validation errror - " + name, e);
            return new Validation(false,
                    "validation error - " + name + " " + e.getMessage());
        }

    }


}
