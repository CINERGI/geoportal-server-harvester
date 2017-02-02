/*
 * Copyright 2017 Esri, Inc.
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
package com.esri.geoportal.harvester.migration;

import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.DataInputException;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migration broker.
 */
/*package*/ class MigrationBroker implements InputBroker {
  private static final Logger LOG = LoggerFactory.getLogger(MigrationBroker.class);
  private static final int PAGE_SIZE = 10;

  private final MigrationConnector connector;
  private final MigrationBrokerDefinitionAdaptor definition;
  
  private DataSource dataSource;
  private final Map<Integer,String> userMap = new HashMap<>();

  /**
   * Creates instance of the broker.
   * @param connector connector
   * @param definition definition
   */
  public MigrationBroker(MigrationConnector connector, MigrationBrokerDefinitionAdaptor definition) {
    this.connector = connector;
    this.definition = definition;
  }

  @Override
  public URI getBrokerUri() throws URISyntaxException {
    return new URI("MIG",definition.getJndi(),null);
  }

  @Override
  public Iterator iterator(IteratorContext iteratorContext) throws DataInputException {
    return new MigrationIterator(iteratorContext);
  }

  @Override
  public EntityDefinition getEntityDefinition() {
    return definition.getEntityDefinition();
  }

  @Override
  public InputConnector getConnector() {
    return connector;
  }

  @Override
  public void initialize(InitContext context) throws DataProcessorException {
    try {
      initDataSource();
      buildUserMap();
    } catch (NamingException|SQLException ex) {
      throw new DataProcessorException(String.format("Unable to init broker."), ex);
    }
  }

  @Override
  public void terminate() {
  }
  
  private void initDataSource() throws NamingException {
    Context initContext = new InitialContext();
    Context webContext = (Context)initContext.lookup("java:/comp/env");
    dataSource = (DataSource) webContext.lookup(definition.getJndi());  
  }
  
  private void buildUserMap() throws SQLException {
    try (
            Connection conn = dataSource.getConnection(); 
            PreparedStatement st = conn.prepareStatement("SELECT * FROM GPT_USER");
            ResultSet rs = st.executeQuery();
        ) {
      while (rs.next()) {
        Integer userId = rs.getInt("USERID");
        String userName = rs.getString("USERNAME");
        userMap.put(userId, userName);
      }
    }
  }
  
  private class MigrationIterator implements InputBroker.Iterator {
    private final IteratorContext iteratorContext;

    public MigrationIterator(IteratorContext iteratorContext) {
      this.iteratorContext = iteratorContext;
    }

    @Override
    public boolean hasNext() throws DataInputException {
      // TODO implement hasNext() for MigrationIterator
      return false;
    }

    @Override
    public DataReference next() throws DataInputException {
      // TODO implement next() for MigrationIterator
      return null;
    }
    
  }
}
