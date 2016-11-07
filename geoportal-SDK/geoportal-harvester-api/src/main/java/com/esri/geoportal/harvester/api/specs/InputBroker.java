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
package com.esri.geoportal.harvester.api.specs;

import com.esri.geoportal.harvester.api.Broker;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.ex.DataInputException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Input broker.
 * <p>
 * Provides mechanism to obtain data.
 * 
 * @see InputConnector
 */
public interface InputBroker extends Broker<InputConnector> {
  
  /**
   * Gets broker URI.
   * @return broker URI
   * @throws URISyntaxException if error generating broker URI
   */
  URI getBrokerUri() throws URISyntaxException;
  
  /**
   * Gets iterator.
   * @param iteratorContext iterator context
   * @return iterator
   * @throws DataInputException if error creating iterator.
   */
  Iterator iterator(IteratorContext iteratorContext) throws DataInputException;
  
  /**
   * Iterator.
   */
  interface Iterator {
    /**
     * Checks if more data available.
     * @return <code>true</code> if more data available
     * @throws DataInputException if checking if more data available fails
     */
    boolean hasNext() throws DataInputException;

    /**
     * Gets next available data reference.
     * @return data reference
     * @throws DataInputException if getting next data reference fails
     */
    DataReference next() throws DataInputException;
  }
  
  /**
   * Iterator context.
   */
  interface IteratorContext {
    /**
     * Gets last harvest date.
     * @return last harvest date or <code>null</code> if no last harvest date available
     */
    Date getLastHarvestDate();
  }
}
