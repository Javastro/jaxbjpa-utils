package org.javastro.ivoa.jaxb;
/*
 * Created on 26/09/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import jakarta.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A SchemaOutputResolver which can be given a map of namespaces to filenames. If it does not
 * find the namespace in the map it attempts to form a filename from the last part of the namepace URI. 
 */
public class SchemaNamer  extends  SchemaOutputResolver {
   /** logger for this class */
   private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
         .getLogger(SchemaOutputResolver.class);
   private final Map<String,String> names;
   SchemaNamer() {
       this.names = new HashMap<>();
   }
   public SchemaNamer(Map<String,String> n) {
      this.names = n;
   }

   @Override
   public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
      String n;
      if(names.containsKey(namespaceUri) && !names.get(namespaceUri).isEmpty()) {
         n = names.get(namespaceUri);
      }
      else {
         n = Arrays.stream(namespaceUri.split("/+")).filter(s -> s.length() > 0).map(s -> s+".xsd").reduce((first, second) -> second).orElse(suggestedFileName);
      }
      logger.info("schema namespace {} being written to {}",namespaceUri,n);
      return new StreamResult(n);
   }
}
