package edu.sdsc.oai.source;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by valentin on 9/5/16.
 */
public  class OAITransformer {
    private static final TransformerFactory factory = TransformerFactory.newInstance();
    private String location="oai_xslt/xoai2oai_dc.xsl";

    public Transformer transformer () {
        try {

            Enumeration<URL> enn = this.getClass().getClassLoader().getResources("oai_xslt/");
            URL test =enn.nextElement();
            return factory.newTransformer(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(location)));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Unable to load resource file '"+location+"'", e);
        } catch (Exception ex){
            throw new RuntimeException("Unable to load resource file '"+location+"'", ex);
        }
    }
}
