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
package com.esri.geoportal.harvester.support;

import com.esri.geoportal.harvester.api.DataDestinationException;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.DataSourceException;
import com.esri.geoportal.harvester.engine.Process;
import com.esri.geoportal.harvester.engine.ReportBuilder;
import java.util.Calendar;
import java.util.Date;

/**
 * Report statistics.
 */
public class ReportStatistics implements ReportBuilder {
  private Date startDate;
  private Date endDate;
  
  private long succeeded;
  private long harvestFailed;
  private long publishFailed;
  
  private boolean failure;

  /**
   * Gets start date.
   * @return start date
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Gets end date.
   * @return end date
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Gets number of successfully processed records.
   * @return number of successfully processed records
   */
  public long getSucceeded() {
    return succeeded;
  }

  /**
   * Gets number of records which failed to be harvested.
   * @return number of harvestFailed records
   */
  public long getHarvestFailed() {
    return harvestFailed;
  }

  /**
   * Gets number of records which failed to be published.
   * @return number of records which failed to be published
   */
  public long getPublishFailed() {
    return publishFailed;
  }

  /**
   * Checks if there was a general error.
   * @return <code>true</code> if there was a general error
   */
  public boolean isFailure() {
    return failure;
  }

  @Override
  public void started(Process process) {
    startDate = Calendar.getInstance().getTime();
  }

  @Override
  public void completed(Process process) {
    endDate = Calendar.getInstance().getTime();
  }

  @Override
  public void success(Process process, DataReference dataReference) {
    ++succeeded;
  }

  @Override
  public void harvestError(Process process, DataSourceException ex) {
    ++harvestFailed;
  }

  @Override
  public void publishError(Process process, DataDestinationException ex) {
    ++publishFailed;
  }

  @Override
  public void generalError(Process process, String message) {
    failure = true;
  }
  
}