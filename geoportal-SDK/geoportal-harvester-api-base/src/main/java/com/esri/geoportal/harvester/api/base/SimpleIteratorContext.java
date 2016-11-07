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
package com.esri.geoportal.harvester.api.base;

import com.esri.geoportal.harvester.api.specs.InputBroker.IteratorContext;
import java.util.Date;

/**
 * Simple iterator context.
 */
public class SimpleIteratorContext implements IteratorContext {
  private Date lastHarvestDate;

  @Override
  public Date getLastHarvestDate() {
    return lastHarvestDate;
  }

  public void setLastHarvest(Date lastHarvestDate) {
    this.lastHarvestDate = lastHarvestDate;
  }
  
}
