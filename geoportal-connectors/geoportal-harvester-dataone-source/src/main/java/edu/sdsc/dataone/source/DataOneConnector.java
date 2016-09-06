package edu.sdsc.dataone.source;

import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.UITemplate;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;
import org.dataone.service.exceptions.ServiceFailure;

import static edu.sdsc.dataone.source.DataOneBrokerDefinitionAdaptor.P_FORMATIDENTIFIER;
import static edu.sdsc.dataone.source.DataOneBrokerDefinitionAdaptor.P_HOST_URL;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 8/30/16.
 */
public class DataOneConnector implements InputConnector<InputBroker> {
    public static final String TYPE = "DataOne";

    public InputBroker createBroker(EntityDefinition definition) throws InvalidDefinitionException {

        return new DataOneBroker(this, new DataOneBrokerDefinitionAdaptor(definition));
    }

    public String getType() {
        return TYPE;
    }

    public UITemplate getTemplate() {
        List<UITemplate.Argument> args = new ArrayList<>();
        //default "https://cn.dataone.org/cn"
        args.add(new UITemplate.StringArgument(P_HOST_URL, "DataOne CNode URL",true));
        args.add(new UITemplate.StringArgument(P_FORMATIDENTIFIER, "DataOne Format Identifier",true));

        return new UITemplate(getType(), "DataOne", args);
    }
}
