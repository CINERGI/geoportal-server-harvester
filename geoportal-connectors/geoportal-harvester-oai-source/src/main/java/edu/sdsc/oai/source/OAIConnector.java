package edu.sdsc.oai.source;

import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.UITemplate;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;


import static edu.sdsc.oai.source.OAIBrokerDefinitionAdaptor.P_METAFORMAT;
import static edu.sdsc.oai.source.OAIBrokerDefinitionAdaptor.P_HOST_URL;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 8/30/16.
 */
public class OAIConnector implements InputConnector<InputBroker> {
    public static final String TYPE = "OAI";

    public InputBroker createBroker(EntityDefinition definition) throws InvalidDefinitionException {

        return new OAIBroker(this, new OAIBrokerDefinitionAdaptor(definition));
    }

    public String getType() {
        return TYPE;
    }

    public UITemplate getTemplate() {
        List<UITemplate.Argument> args = new ArrayList<>();
        //default "https://cn.dataone.org/cn"
        args.add(new UITemplate.StringArgument(P_HOST_URL, "Open Archives URL",true));
        args.add(new UITemplate.StringArgument(P_METAFORMAT, "OpenArchives MetadataForma",false));

        return new UITemplate(getType(), "OpenArchives (OAI)", args);
    }
}
