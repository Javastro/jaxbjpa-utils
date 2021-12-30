/*
 * Created on 18 Aug 2021 
 * Copyright 2021 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */

package org.javastro.ivoa.jaxb;

import java.lang.annotation.Annotation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

/**
 * Create the required entity metadata assuming JAXB Annotation.
 * 
 * @author Paul Harrison (paul.harrison@manchester.ac.uk)
 * @since 18 Aug 2021
 */
public class JaxbAnnotationMeta<T> implements EntityMetadata<T> {

    private final Class<T> type;

    public JaxbAnnotationMeta(Class<T> type) {
        this.type = type;
    }

    public static <T> JaxbAnnotationMeta<T> of(Class<T> clazz) {
        return new JaxbAnnotationMeta<T>(clazz);
    }

    /**
     * {@inheritDoc} overrides @see
     * org.javastro.ivoa.entities.jaxb.EntityMetadata#getJavaType()
     */
    @Override
    public Class<T> getJavaType() {
        return type;

    }

    /**
     * {@inheritDoc} overrides @see
     * org.javastro.ivoa.entities.jaxb.EntityMetadata#getNamespace()
     */
    @Override
    public String getNamespace() {
        String nsURI = "";
        for (Annotation annotation : type.getPackage().getAnnotations()) {
            if (annotation.annotationType() == XmlSchema.class) {
                nsURI = ((XmlSchema) annotation).namespace();
                break;
            }
        }
        return nsURI;
    }

    /**
     * {@inheritDoc} overrides @see
     * org.javastro.ivoa.entities.jaxb.EntityMetadata#getName()
     */
    @Override
    public String getName() {
        return type.getSimpleName();
    }
    
    public JAXBElement<T> element(T o)
    {
        return new JAXBElement<T>(new QName(getNamespace(), getName()), type, o);
    }

}
