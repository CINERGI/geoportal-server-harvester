package edu.sdsc.oai.source;

import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.Task;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.base.DataCollector;
import com.esri.geoportal.harvester.api.base.DataPrintStreamOutput;
import com.esri.geoportal.harvester.api.base.SimpleInitContext;
import java.net.URL;
import java.util.Arrays;

import com.esri.geoportal.harvester.console.*;

/**
 * Created by valentin on 8/30/16.
 * cn: https://cn.dataone.org/
 */
public class OAIApplication {

    /**
     * Man method
     * @param args arguments: OAI URL, MetdataFormatPrefix
     * @throws Exception if any exception occurs
     */
    public static void main(String[] args) throws Exception {
        DataPrintStreamOutput destination = new DataPrintStreamOutput(System.out);
        ConsoleConnector conCon = new ConsoleConnector();
        EntityDefinition conDef = new EntityDefinition();
        ConsoleBrokerDefinitionAdaptor conAdpt = new ConsoleBrokerDefinitionAdaptor(conDef);

        OAIConnector connector = new OAIConnector();
        URL start = null;
        String formatIdentifier = null;
        if (args.length > 0 && args.length == 2) {
             start = new URL(args[0]);
            formatIdentifier = args[1];
         } else {
             start =  new URL( "http://oai.datacite.org/oai"  );
            formatIdentifier = "oai_dc";
        }
        //URL start = new URL(args[0]);
        EntityDefinition def = new EntityDefinition();
        OAIBrokerDefinitionAdaptor adaptor = new OAIBrokerDefinitionAdaptor(def);
        adaptor.setHostUrl(start);
        adaptor.setMetadataFormatPrefix(formatIdentifier);


        InputBroker d1ib = null;
        try {
            d1ib = connector.createBroker(def);
            d1ib.initialize(new SimpleInitContext(new Task(null, d1ib, null)));
//            DataCollector dataCollector =
//                    new DataCollector(d1ib,
//                            Arrays.asList(new DataPrintStreamOutput[]{destination}));
            DataCollector dataCollector =
                    new DataCollector(d1ib,
                            Arrays.asList(
                                    conCon.createBroker(conDef)
                            )
                    )
                    ;
            dataCollector.collect();
        } finally {
            if (d1ib != null) {
                d1ib.terminate();
            }
        }
    }

}
