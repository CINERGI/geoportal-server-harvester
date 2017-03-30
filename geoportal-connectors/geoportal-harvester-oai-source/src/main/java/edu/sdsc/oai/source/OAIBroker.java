package edu.sdsc.oai.source;

import com.esri.geoportal.commons.constants.MimeType;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.base.SimpleDataReference;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.DataInputException;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;
import com.lyncode.xoai.model.oaipmh.Header;
import com.lyncode.xoai.model.oaipmh.ListRecords;
import com.lyncode.xoai.model.oaipmh.Metadata;
import com.lyncode.xoai.model.oaipmh.Record;
import com.lyncode.xoai.serviceprovider.ServiceProvider;
import com.lyncode.xoai.serviceprovider.client.HttpOAIClient;
import com.lyncode.xoai.serviceprovider.client.OAIClient;
import com.lyncode.xoai.serviceprovider.model.Context;
import com.lyncode.xoai.serviceprovider.parameters.ListRecordsParameters;
import com.lyncode.xoai.xml.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by valentin on 8/30/16.
 */
public class OAIBroker implements InputBroker  {
    private static final Logger LOG = LoggerFactory.getLogger(OAIBroker.class);

    private final OAIConnector connector;
    private final OAIBrokerDefinitionAdaptor definition;

    private ServiceProvider client;

    private ListRecords objectList;
    private String metadataFormat;

    private java.util.Iterator<Record> recs;
    private int start = 1;
    private boolean noMore;
    private int PAGE_SIZE = 10;

    /**
     * Creates instance of the broker.
     * @param connector connector
     * @param definition definition
     */
    public OAIBroker(OAIConnector connector, OAIBrokerDefinitionAdaptor definition) {
        this.connector = connector;
        this.definition = definition;

    }
    @Override
    public URI getBrokerUri() throws URISyntaxException {
        return new URI("OAI",definition.getHostUrl().toExternalForm(),definition.getMetadataFormatPrefix());

    }
    @Override
    public Iterator iterator(IteratorContext iteratorContext) throws DataInputException {
      return new OAIIterator(iteratorContext);

    }
    @Override
    public EntityDefinition getEntityDefinition() {
        return definition.getEntityDefinition();
    }
    @Override
    public InputConnector getConnector(){
        return connector;
    }

    @Override
    public void initialize(InitContext context) throws DataProcessorException {
        try {
            OAIClient httpOaiCLient = new HttpOAIClient(definition.getHostUrl().toExternalForm());
            //Granularity granularity = new Granularity();
            //Transformer xsl =  Context.KnownTransformer.OAI_DC.transformer();
            Context config = new Context().withBaseUrl(definition.getHostUrl().toExternalForm())
                    .withOAIClient(httpOaiCLient)
                    .withMetadataTransformer("oai_dc",Context.KnownTransformer.OAI_DC );

            client = new ServiceProvider(config);
            ListRecordsParameters p = new ListRecordsParameters()
                    .withMetadataPrefix(definition.getMetadataFormatPrefix());
            recs= client.listRecords(p);
            LOG.info("Intializing xOAI Cnode with:"+definition.getHostUrl().toString());
        } catch (Exception serviceFailure){
            serviceFailure.printStackTrace();
            throw new DataProcessorException("cannot connect to xOAI" );

        }

    }

    @Override
    public void terminate() {
        if (client != null ){

            client = null;
        }


    }
    @Override
    public String toString() {
        return String.format("OAI [%s]", definition.getHostUrl());
    }

    /**
     * DataOne iterator.
     */
    private class OAIIterator implements InputBroker.Iterator {
        Transformer transformer = new OAITransformer().transformer();
//        ObjectList objectList  = client.listObjects(session, null, null, null,
//                null,null,null, null);
//        ArrayList pids = new ArrayList<Identi;fier>();
//        for (ObjectInfo o: objectList.getObjectInfoList()) {
//            pids.add(o.getIdentifier());
//        }
 //       return pids.iterator();
        private final IteratorContext iteratorContext;

        /**
         * Creates instance of the iterator.
         * @param iteratorContext iterator context
         */
        public OAIIterator(IteratorContext iteratorContext) {
            this.iteratorContext = iteratorContext;
        }

        @Override
        public boolean hasNext() throws DataInputException {
            try {
                return recs.hasNext();


            } catch (Exception ex) {
                throw new DataInputException(OAIBroker.this, "Error reading data.", ex);
            }
        }

        @Override
        public DataReference next() throws DataInputException {
            String identifier ="";
            try {
                Record rec =  recs.next();
                identifier = rec.getHeader().getIdentifier();
                if (rec.getHeader().getStatus() != Header.Status.DELETED ) {

                    //XOAIMetadata xo = rec.getMetadata().getValue();
                    Metadata xo = rec.getMetadata();
                    java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

                    String xmlString = XmlWriter.toString(xo);
                    ByteArrayInputStream inputStream= new ByteArrayInputStream(xmlString.getBytes());
                    transformer.transform(new StreamSource(inputStream),new StreamResult(outputStream));
                    SimpleDataReference ref = new SimpleDataReference(getBrokerUri(),
                            definition.getEntityDefinition().getLabel(),
                            identifier,
                            rec.getHeader().getDatestamp(),
                            new URI("uuid", rec.getHeader().getIdentifier().toString(), null)
                    );
                    ref.addContext(MimeType.APPLICATION_XML, outputStream.toByteArray());
                    return ref;
//                    return new SimpleDataReference(
//                            OAIBroker.this.getBrokerUri(), // broker
//                            getEntityDefinition().getLabel(), //broker label
//                            identifier, //record identifier
//                            rec.getHeader().getDatestamp(),
//                            new URI("uuid", rec.getHeader().getIdentifier().toString(), null), // source uri
//                            outputStream.toByteArray(),
//                            MimeType.APPLICATION_XML);

                } else {
                    LOG.info("record deleted:" + identifier);
                    return next();
                }

            } catch (Exception ex) {
                throw new DataInputException(OAIBroker.this, "Error reading data.", ex);
            }
        }
    }
}
