package edu.sdsc.dataone.source;

import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.UITemplate;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static edu.sdsc.dataone.source.DataOneConstants.P_CN_URL;
import static edu.sdsc.dataone.source.DataOneConstants.P_FORMAT_IDENTIFIER;

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



    @Override
    public UITemplate getTemplate(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("DataOneSource", locale);
        List<UITemplate.Argument> arguments = new ArrayList<>();
        //default "https://cn.dataone.org/cn"
        arguments.add(new UITemplate.StringArgument(P_CN_URL, bundle.getString("dataone.cnUrl"), true){
            @Override
            public String getHint() {
                return bundle.getString("dataone.urlhint");
            }
        });
        arguments.add(new UITemplate.StringArgument(P_FORMAT_IDENTIFIER, bundle.getString("dataone.formatid"), true){
            @Override
            public String getHint() {
                return bundle.getString("dataone.formathint");
            }
        });
        return new UITemplate(getType(), bundle.getString("dataone"), arguments);

    }
}
