/*
 * Created on 18 Aug 2021 
 * Copyright 2021 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.javastro.ivoa.jaxb;

/**
 * Entity Metadata Required to generify some JAXB operations .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 18 Aug 2021
 */
public interface EntityMetadata<T> {

    
        Class<T> getJavaType();
        String getNamespace();
        /**
         * Element name
         * @return
         */
        String getName();

}


