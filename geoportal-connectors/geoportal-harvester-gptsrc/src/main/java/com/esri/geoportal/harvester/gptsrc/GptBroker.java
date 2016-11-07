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
package com.esri.geoportal.harvester.gptsrc;

import com.esri.geoportal.commons.constants.MimeType;
import com.esri.geoportal.commons.gpt.client.Client;
import com.esri.geoportal.commons.gpt.client.EntryRef;
import com.esri.geoportal.commons.http.BotsHttpClient;
import com.esri.geoportal.commons.robots.Bots;
import com.esri.geoportal.commons.robots.BotsUtils;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.base.BotsBrokerDefinitionAdaptor;
import com.esri.geoportal.harvester.api.base.SimpleDataReference;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.DataInputException;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gpt input broker.
 */
/*package*/class GptBroker implements InputBroker {
  private static final Logger LOG = LoggerFactory.getLogger(GptBroker.class);
  private final GptConnector connector;
  private final GptBrokerDefinitionAdaptor definition;

  private Client client;
  
  public GptBroker(GptConnector connector, GptBrokerDefinitionAdaptor definition) {
    this.connector = connector;
    this.definition = definition;
  }
  

  @Override
  public void initialize(InitContext context) throws DataProcessorException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    if (context.getTask().getTaskDefinition().isIgnoreRobotsTxt()) {
      client = new Client(httpclient, definition.getHostUrl(), definition.getCredentials());
    } else {
      Bots bots = BotsUtils.readBots(definition.getBotsConfig(), httpclient, definition.getBotsMode(), definition.getHostUrl());
      client = new Client(new BotsHttpClient(httpclient,bots), definition.getHostUrl(), definition.getCredentials());
    }
  }

  @Override
  public void terminate() {
    try {
      client.close();
    } catch (IOException ex) {
      LOG.error(String.format("Error terminating broker."), ex);
    }
  }

  @Override
  public URI getBrokerUri() throws URISyntaxException {
    return new URI("GPT",definition.getHostUrl().toExternalForm(),null);
  }

  @Override
  public String toString() {
    return String.format("GPT [%s]", definition.getHostUrl());
  }

  @Override
  public InputConnector getConnector() {
    return connector;
  }

  @Override
  public EntityDefinition getEntityDefinition() {
    return definition.getEntityDefinition();
  }

  @Override
  public Iterator iterator(IteratorContext iteratorContext) throws DataInputException {
    return new GptIterator();
  }
  
  private class GptIterator implements InputBroker.Iterator {
    private java.util.Iterator<String> iter;

    @Override
    public boolean hasNext() throws DataInputException {
      try {
        if (iter==null) {
          iter = null;
          List<String> list = client.listIds();
          if (list==null || list.isEmpty()) {
            return false;
          }
          iter = list.iterator();
        }
        return iter.hasNext();
      } catch (IOException|URISyntaxException ex) {
        throw new DataInputException(GptBroker.this, String.format("Error iterating through Geoportal Server 2.0 records."), ex);
      }
    }

    @Override
    public DataReference next() throws DataInputException {
      if (iter==null || !iter.hasNext()) {
        throw new DataInputException(GptBroker.this, String.format("Error iterating through Geoportal Server 2.0 records."));
      }
      String id = iter.next();
      try {
        String xml = client.readXml(id);
        EntryRef ref = client.readItem(id);
        return new SimpleDataReference(getBrokerUri(), getEntityDefinition().getLabel(), ref.getId(), ref.getLastModified(), ref.getSourceUri(), xml.getBytes("UTF-8"), MimeType.APPLICATION_XML);
      } catch (URISyntaxException|IOException ex) {
        throw new DataInputException(GptBroker.this, String.format("Error iterating through Geoportal Server 2.0 records."), ex);
      }
    }
    
  }
}
