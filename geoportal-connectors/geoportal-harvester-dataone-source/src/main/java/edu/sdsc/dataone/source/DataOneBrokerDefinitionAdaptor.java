/*
 * Copyright 2016 Esri, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sdsc.dataone.source;

import com.esri.geoportal.harvester.api.base.BrokerDefinitionAdaptor;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import org.apache.commons.lang3.StringUtils;
import org.dataone.service.types.v1.ObjectFormatIdentifier;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * UNC broker definition adaptor.
 */
public class DataOneBrokerDefinitionAdaptor extends BrokerDefinitionAdaptor {
  public static final String P_HOST_URL = "dataone-host-url";
  public static final String P_FORMATIDENTIFIER = "dataone-formatidentifier";
  private URL hostUrl;
  private ObjectFormatIdentifier objectFormat;

  /**
   * Creates instance of the adaptor.
   * @param def broker definition
   * @throws InvalidDefinitionException if invalid broker definition
   */
  public DataOneBrokerDefinitionAdaptor(EntityDefinition def) throws InvalidDefinitionException {
    super(def);
    if (StringUtils.trimToEmpty(def.getType()).isEmpty()) {
      def.setType(DataOneConnector.TYPE);
    } else if (!DataOneConnector.TYPE.equals(def.getType())) {
      throw new InvalidDefinitionException("Broker definition doesn't match");
    } else {
        try {
          hostUrl = new URL(get(P_HOST_URL));
        } catch (MalformedURLException ex) {
          throw new InvalidDefinitionException(String.format("Invalid %s: %s", P_HOST_URL, get(P_HOST_URL)), ex);
        }
      try {
        objectFormat = new ObjectFormatIdentifier();
        objectFormat.setValue(get(P_FORMATIDENTIFIER));
      } catch (Exception ex) {
        throw new InvalidDefinitionException(String.format("Invalid %s: %s", P_FORMATIDENTIFIER, get(P_FORMATIDENTIFIER)), ex);
      }
    }
  }

  /**
   * Gets host url.
   *
   * @return host url
   */
  public URL getHostUrl() {
    return hostUrl;
  }

  /**
   * Sets host url.
   *
   * @param url host url
   */
  public void setHostUrl(URL url) {
    this.hostUrl = url;
    set(P_HOST_URL, url.toExternalForm());
  }

  public ObjectFormatIdentifier getObjectFormatIdentifier() {

    return objectFormat;
  }

  public void setObjectIdentifier(String objectIdentifierString){
    ObjectFormatIdentifier oi = new ObjectFormatIdentifier();
    oi.setValue(objectIdentifierString);

    this.objectFormat = oi;
    set(P_FORMATIDENTIFIER, objectIdentifierString);

  }
}
