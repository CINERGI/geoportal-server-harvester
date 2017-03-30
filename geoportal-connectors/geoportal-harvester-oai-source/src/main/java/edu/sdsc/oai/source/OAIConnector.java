package edu.sdsc.oai.source;

import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.UITemplate;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static edu.sdsc.oai.source.OAIConstants.P_HOST_URL;
import static edu.sdsc.oai.source.OAIConstants.P_METAFORMAT;

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

    public UITemplate getTemplate(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("OAISource", locale);
        List<UITemplate.Argument> arguments = new ArrayList<>();
        //default "https://cn.dataone.org/cn"
        arguments.add(new UITemplate.StringArgument(P_HOST_URL,
                bundle.getString("oai.url"), true){
            @Override
            public String getHint() {
                return bundle.getString("oai.urlhint");
            }
        });
        arguments.add(new UITemplate.StringArgument(P_METAFORMAT,
                bundle.getString("oai.format"), true){
            @Override
            public String getHint() {
                return bundle.getString("oai.formathint");
            }
        });
        return new UITemplate(getType(), bundle.getString("oai"), arguments);



    }
}
