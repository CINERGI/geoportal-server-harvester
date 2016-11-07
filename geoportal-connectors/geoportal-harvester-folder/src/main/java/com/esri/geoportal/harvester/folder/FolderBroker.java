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
package com.esri.geoportal.harvester.folder;

import com.esri.geoportal.harvester.api.ex.DataOutputException;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.base.BaseProcessInstanceListener;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.defs.PublishingStatus;
import com.esri.geoportal.harvester.api.ex.DataException;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.harvester.api.specs.OutputBroker;
import com.esri.geoportal.harvester.api.specs.OutputConnector;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.io.OutputStream;
import java.net.URI;
import static com.esri.geoportal.harvester.folder.PathUtil.splitPath;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Folder broker.
 */
/*package*/ class FolderBroker implements OutputBroker {
  private final static Logger LOG = LoggerFactory.getLogger(FolderBroker.class);
  private final FolderConnector connector;
  private final FolderBrokerDefinitionAdaptor definition;
  private final Set<String> existing = new HashSet<>();
  private volatile boolean preventCleanup;

  /**
   * Creates instance of the broker.
   * @param connector connector
   * @param definition broker definition
   */
  public FolderBroker(FolderConnector connector, FolderBrokerDefinitionAdaptor definition) {
    this.connector = connector;
    this.definition = definition;
  }

  @Override
  public void initialize(InitContext context) throws DataProcessorException {
    if (definition.getCleanup()) {
      context.addListener(new BaseProcessInstanceListener() {
        @Override
        public void onError(DataException ex) {
          preventCleanup = true;
        }
      });
      File rootFolder = definition.getRootFolder();
      fetchExisting(rootFolder);
    }
  }
  
  private void fetchExisting(File folder) {
    for (File f: folder.listFiles()) {
      if (f.isFile()) {
        existing.add(f.getAbsolutePath());
      } else if (f.isDirectory()) {
        fetchExisting(f);
      }
    }
  }

  @Override
  public void terminate() {
    if (definition.getCleanup() && !preventCleanup) {
      existing.forEach(f->new File(f).delete());
      LOG.info(String.format("%d records has been removed during cleanup.", existing.size()));
    }
  }

  @Override
  public OutputConnector getConnector() {
    return connector;
  }

  @Override
  public PublishingStatus publish(DataReference ref) throws DataOutputException {
      File f = generateFileName(ref.getBrokerUri(), ref.getSourceUri(), ref.getId());
      boolean created = !f.exists();
      f.getParentFile().mkdirs();
      try (OutputStream output = new FileOutputStream(f);) {
        output.write(ref.getContent());
        existing.remove(f.getAbsolutePath());
        return created? PublishingStatus.CREATED: PublishingStatus.UPDATED;
      } catch (Exception ex) {
        throw new DataOutputException(this,String.format("Error publishing data: %s", ref), ex);
      }
  }

  @Override
  public String toString() {
    return String.format("FOLDER [%s]", definition.getRootFolder());
  }
  
  private File generateFileName(URI brokerUri, URI sourceUri, String id) {
    URI ssp = URI.create(brokerUri.getSchemeSpecificPart());
    String root = StringUtils.defaultIfEmpty(ssp.getHost(), ssp.getPath());
    File rootFolder = definition.getRootFolder().toPath().resolve(root).toFile();
    
    File fileName = rootFolder;
    if (sourceUri.getPath()!=null) {
      List<String> subFolder = splitPath(sourceUri.getPath().replaceAll("/[a-zA-Z]:/|/$", ""));
      if (!subFolder.isEmpty() && subFolder.get(0).equals(root)) {
        subFolder.remove(0);
      }
      for (String sf : subFolder) {
        fileName = new File(fileName, sf);
      }
      if (!fileName.getName().contains(".")) {
        fileName = fileName.getParentFile().toPath().resolve(fileName.getName()+".xml").toFile();
      }
    } else {
      fileName = new File(fileName,id+".xml");
    }
    
    return fileName;
  }

  @Override
  public EntityDefinition getEntityDefinition() {
    return definition.getEntityDefinition();
  }
  
}
