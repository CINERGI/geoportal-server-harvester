/*
 * Copyright 2016 Esri, Inc..
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
package com.esri.geoportal.harvester.waf;

import com.esri.geoportal.commons.http.BotsHttpClient;
import com.esri.geoportal.commons.http.BotsHttpClientFactory;
import com.esri.geoportal.commons.robots.Bots;
import com.esri.geoportal.commons.robots.BotsUtils;
import com.esri.geoportal.harvester.api.DataAdaptorDefinition;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.DataSource;
import com.esri.geoportal.harvester.api.DataSourceException;
import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.apache.http.impl.client.HttpClients;

/**
 * WAF data source.
 */
public class WafDataSource implements DataSource<String>, Closeable  {

  private final WafAttributesAdaptor attributes;
  private final Set<URL> visited = new HashSet<>();
  
  private BotsHttpClient httpClient;
  private LinkedList<WafFolder> subFolders;
  private LinkedList<WafFile> files;

  /**
   * Creates instance of the source.
   *
   * @param attributes attributes
   */
  public WafDataSource(WafAttributesAdaptor attributes) {
    this.attributes = attributes;
  }

  @Override
  public DataAdaptorDefinition getDefinition() {
    DataAdaptorDefinition def = new DataAdaptorDefinition();
    def.setType("WAF");
    def.setAttributes(attributes);
    return def;
  }

  @Override
  public String getDescription() {
    return String.format("WAF [%s]", attributes.getHostUrl());
  }

  @Override
  public boolean hasNext() throws DataSourceException {
    
    try {
      assertExecutor();

      if (files!=null && !files.isEmpty()) {
        return true;
      }

      if (subFolders!=null && !subFolders.isEmpty()) {
        WafFolder subFolder = subFolders.poll();
        if (visited.contains(subFolder.getFolderUrl())) {
          return hasNext();
        }
        visited.add(subFolder.getFolderUrl());
        WafFolderContent content = subFolder.readContent(httpClient);
        content.getSubFolders().forEach(f->subFolders.offer(f));
        files = new LinkedList<>(content.getFiles());
        return hasNext();
      }
      
      if (subFolders==null) {
        URL startUrl = new URL(attributes.getHostUrl().toExternalForm().replaceAll("/$", "")+"/");
        WafFolderContent content = new WafFolder(startUrl).readContent(httpClient);
        subFolders = new LinkedList<>(content.getSubFolders());
        files = new LinkedList<>(content.getFiles());
        return hasNext();
      }
      
      return false;
    } catch (IOException|URISyntaxException ex) {
      throw new DataSourceException(this, "Error reading data.", ex);
    }
  }

  @Override
  public DataReference<String> next() throws DataSourceException {
    try {
      assertExecutor();
      WafFile file = files.poll();
      return file.readContent(httpClient);
    } catch (IOException ex) {
      throw new DataSourceException(this, "Error reading data.", ex);
    }
  }

  /**
   * Asserts executor.
   * @throws IOException if creating executor fails
   */
  private void assertExecutor() {
    if (httpClient==null) {
      Bots bots = BotsUtils.readBots(attributes.getBotsConfig(), HttpClients.createDefault(), attributes.getBotsMode(), attributes.getHostUrl());
      httpClient = BotsHttpClientFactory.STD.create(bots);
    }
  }

  @Override
  public void close() throws IOException {
    if (httpClient!=null) {
      httpClient.close();
    }
  }

  @Override
  public String toString() {
    return getDescription();
  }
}
