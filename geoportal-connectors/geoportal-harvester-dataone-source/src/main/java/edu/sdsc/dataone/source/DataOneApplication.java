package edu.sdsc.dataone.source;

import com.esri.geoportal.harvester.api.ProcessInstance;
import com.esri.geoportal.harvester.api.base.DataCollector;
import com.esri.geoportal.harvester.api.base.DataPrintStreamOutput;
import com.esri.geoportal.harvester.api.base.SimpleInitContext;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.Task;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import org.dataone.configuration.Settings;
import org.dataone.service.types.v1.ObjectFormatIdentifier;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by valentin on 8/30/16.
 * cn: https://cn.dataone.org/
 */
public class DataOneApplication {

    /**
     * Man method
     * @param args arguments: DataOne Coordinating Node URL
     * @throws Exception if any exception occurs
     */
    public static void main(String[] args) throws Exception {
        DataPrintStreamOutput destination = new DataPrintStreamOutput(System.out);

        DataOneConnector connector = new DataOneConnector();
        URL start = null;
        ObjectFormatIdentifier formatIdentifier = null;
        if (args.length > 0 && args.length == 2) {
             start = new URL(args[0]);
            formatIdentifier = new ObjectFormatIdentifier();
            formatIdentifier.setValue(args[1]);
         } else {
             start =  new URL( (String)Settings.getConfiguration().getString("CN_URL" ) );
            formatIdentifier = new ObjectFormatIdentifier();
            formatIdentifier.setValue("http://www.isotc211.org/2005/gmd-noaa");
        }
        //URL start = new URL(args[0]);
        EntityDefinition def = new EntityDefinition();
        DataOneBrokerDefinitionAdaptor adaptor = new DataOneBrokerDefinitionAdaptor(def);
        adaptor.setHostUrl(start);


        InputBroker d1ib = null;
        try {
            ArrayList<ProcessInstance.Listener> listeners = new ArrayList<>();
            d1ib = connector.createBroker(def);
            d1ib.initialize(new SimpleInitContext(new Task(null, d1ib, null), listeners));
            DataCollector dataCollector =
                    new DataCollector(d1ib,
                            Arrays.asList(new DataPrintStreamOutput[]{destination}), listeners);
            dataCollector.collect();
        } finally {
            if (d1ib != null) {
                d1ib.terminate();
            }
        }
    }

}
