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
package com.esri.geoportal.harvester.rest;

import com.esri.geoportal.harvester.engine.TaskDefinition;
import java.util.UUID;

/**
 * Task info.
 */
public class TaskInfo {
  private final UUID id;
  private final TaskDefinition taskDefinition;

  /**
   * Creates instance of the task info.
   * @param id  id of the task info
   * @param taskDefinition task definition
   */
  public TaskInfo(UUID id, TaskDefinition taskDefinition) {
    this.id = id;
    this.taskDefinition = taskDefinition;
  }

  /**
   * Gets id of the task info.
   * @return id
   */
  public UUID getId() {
    return id;
  }

  /**
   * Gets task definition.
   * @return task definition
   */
  public TaskDefinition getTaskDefinition() {
    return taskDefinition;
  }
  
  @Override
  public String toString() {
    return String.format("TASK INFO :: id: %s, taskDefinition: %s", id, taskDefinition);
  }
}