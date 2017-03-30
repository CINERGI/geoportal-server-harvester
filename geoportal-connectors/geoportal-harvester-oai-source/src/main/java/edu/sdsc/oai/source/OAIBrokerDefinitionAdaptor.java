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
package edu.sdsc.oai.source;

import com.esri.geoportal.harvester.api.base.BrokerDefinitionAdaptor;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import org.apache.commons.lang3.StringUtils;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static edu.sdsc.oai.source.OAIConstants.P_HOST_URL;
import static edu.sdsc.oai.source.OAIConstants.P_METAFORMAT;
/**
 * UNC broker definition adaptor.
 */
public class OAIBrokerDefinitionAdaptor extends BrokerDefinitionAdaptor {

  private URL hostUrl;
  private String metadataFormat;

  /**
   * Creates instance of the adaptor.
   * @param def broker definition
   * @throws InvalidDefinitionException if invalid broker definition
   */
  public OAIBrokerDefinitionAdaptor(EntityDefinition def) throws InvalidDefinitionException {
    super(def);
    if (StringUtils.trimToEmpty(def.getType()).isEmpty()) {
      def.setType(OAIConnector.TYPE);
    } else if (!OAIConnector.TYPE.equals(def.getType())) {
      throw new InvalidDefinitionException("Broker definition doesn't match");
    } else {
        try {
          hostUrl = new URL(get(P_HOST_URL));
        } catch (MalformedURLException ex) {
          throw new InvalidDefinitionException(String.format("Invalid %s: %s", P_HOST_URL, get(P_HOST_URL)), ex);
        }
      try {
        metadataFormat = get(P_METAFORMAT);
      } catch (Exception ex) {
        throw new InvalidDefinitionException(String.format("Invalid %s: %s", P_METAFORMAT, get(P_METAFORMAT)), ex);
      }
    }
  }

  @Override
  public void override(Map<String, String> params) {
    consume(params,P_HOST_URL);
    consume(params,P_METAFORMAT);
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

  public String getMetadataFormatPrefix() {

    return metadataFormat;
  }

  public void setMetadataFormatPrefix(String metadataFormat){
    String oi = metadataFormat;

    this.metadataFormat = oi;
    set(P_METAFORMAT, metadataFormat);

  }
}
