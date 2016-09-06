package edu.sdsc.dataone.source;

import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.base.SimpleDataReference;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.DataInputException;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.commons.constants.MimeType;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.dataone.client.v1.CNode;
import org.dataone.client.v1.MNode;
import org.dataone.client.v1.itk.D1Client;
import org.dataone.client.rest.RestClient;
import  org.dataone.client.rest.DefaultHttpMultipartRestClient;
import org.dataone.client.v1.itk.D1Object;
import org.dataone.configuration.Settings;
import org.dataone.service.exceptions.*;
import org.dataone.service.types.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by valentin on 8/30/16.
 */
public class DataOneBroker implements InputBroker  {
    private static final Logger LOG = LoggerFactory.getLogger(DataOneBroker.class);

    private final DataOneConnector connector;
    private final DataOneBrokerDefinitionAdaptor definition;

    private CNode client;
    private Session session;
    private ObjectList objectList;
    private ObjectFormatIdentifier objectFormat;

    private java.util.Iterator<ObjectInfo> recs;
    private int start = 1;
    private boolean noMore;
    private int PAGE_SIZE = 10;

    /**
     * Creates instance of the broker.
     * @param connector connector
     * @param definition definition
     */
    public DataOneBroker(DataOneConnector connector, DataOneBrokerDefinitionAdaptor definition) {
        this.connector = connector;
        this.definition = definition;

    }
    @Override
    public URI getBrokerUri() throws URISyntaxException {
        return new URI("DataOne",definition.getHostUrl().toExternalForm(),definition.getObjectFormatIdentifier().getValue());

    }
    @Override
    public Iterator iterator(Map<String, Object> attributes) throws DataInputException {
      return new DataOneIterator();

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
            session = new Session();
            LOG.info("Intializing D1 Cnode with:"+definition.getHostUrl().toString());
            client = D1Client.getCN(definition.getHostUrl().toString());
        } catch (ServiceFailure serviceFailure){
            throw new DataProcessorException("cannot connect to CN Node");

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
        return String.format("DATAONE [%s]", definition.getHostUrl());
    }

    /**
     * DataOne iterator.
     */
    private class DataOneIterator implements InputBroker.Iterator {

//        ObjectList objectList  = client.listObjects(session, null, null, null,
//                null,null,null, null);
//        ArrayList pids = new ArrayList<Identifier>();
//        for (ObjectInfo o: objectList.getObjectInfoList()) {
//            pids.add(o.getIdentifier());
//        }
 //       return pids.iterator();
        @Override
        public boolean hasNext() throws DataInputException {
            try {
                if (noMore) {
                    return false;
                }

                if (recs==null) {
                    // date, date, objectformatid, replicaStatus, start, count
                    ObjectList ol= client.listObjects( null, null,
                            definition.getObjectFormatIdentifier(),
                            null,start,PAGE_SIZE);
                    LOG.info("d1 ObjectList.Count:"+ol.getCount());
                    List<ObjectInfo> r =
                            ol.getObjectInfoList();

                    if (r.isEmpty()) {
                        noMore = true;
                    } else {
                        recs = r.iterator();
                    }
                    return hasNext();
                }

                if (!recs.hasNext()) {
                    recs = null;
                    start += PAGE_SIZE;
                    return hasNext();
                }
                LOG.info("D1Start:"+start);
                return true;
            } catch (Exception ex) {
                throw new DataInputException(DataOneBroker.this, "Error reading data.", ex);
            }
        }

        @Override
        public DataReference next() throws DataInputException {
            try {
                ObjectInfo rec = recs.next();
                Identifier id = rec.getIdentifier();
                InputStream inputStream = client.get(session,id);
                String metadata = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().collect(Collectors.joining("\n"));
                return new SimpleDataReference(DataOneBroker.this.getBrokerUri(),
                        getEntityDefinition().getLabel(),
                        id.getValue(),
                        rec.getDateSysMetadataModified(),
                        new URI("uuid",
                                id.getValue(),
                                null),
                        metadata.getBytes(), // metadata.getBytes("UTF-8"),
                        MimeType.APPLICATION_XML);
            } catch (NotFound ex) {
                LOG.info("d1. Object Not found at CNODE:" + ex.getMessage());
                return next();
            } catch (NotAuthorized ex) {
                LOG.info("d1. Permisions Error:" + ex.getMessage());
                return next();
            }
            catch (Exception ex) {
                throw new DataInputException(DataOneBroker.this, "Error reading data.", ex);
            }
        }
    }
}
