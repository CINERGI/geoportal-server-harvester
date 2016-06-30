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
package com.esri.geoportal.harvester.engine.impl;

import com.esri.geoportal.harvester.api.defs.TaskDefinition;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.harvester.engine.TasksService;
import com.esri.geoportal.harvester.engine.managers.TaskManager;
import com.esri.geoportal.harvester.engine.support.CrudsException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default tasks service.
 */
public class DefaultTasksService implements TasksService {
  protected final TaskManager taskManager;

  public DefaultTasksService(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  @Override
  public List<Map.Entry<UUID, TaskDefinition>> selectTaskDefinitions(Predicate<? super Map.Entry<UUID, TaskDefinition>> predicate) throws DataProcessorException {
    try {
      return taskManager.select().stream().filter(predicate != null ? predicate : (Map.Entry<UUID, TaskDefinition> e) -> true).collect(Collectors.toList());
    } catch (CrudsException ex) {
      throw new DataProcessorException(String.format("Error selecting task definitions."), ex);
    }
  }

  @Override
  public TaskDefinition readTaskDefinition(UUID taskId) throws DataProcessorException {
    try {
      return taskManager.read(taskId);
    } catch (CrudsException ex) {
      throw new DataProcessorException(String.format("Error reading task definition: %s", taskId), ex);
    }
  }

  @Override
  public boolean deleteTaskDefinition(UUID taskId) throws DataProcessorException {
    try {
      return taskManager.delete(taskId);
    } catch (CrudsException ex) {
      throw new DataProcessorException(String.format("Error deleting task definition: %s", taskId), ex);
    }
  }

  @Override
  public UUID addTaskDefinition(TaskDefinition taskDefinition) throws DataProcessorException {
    try {
      return taskManager.create(taskDefinition);
    } catch (CrudsException ex) {
      throw new DataProcessorException(String.format("Error adding task definition: %s", taskDefinition), ex);
    }
  }

  @Override
  public TaskDefinition updateTaskDefinition(UUID taskId, TaskDefinition taskDefinition) throws DataProcessorException {
    try {
      TaskDefinition oldTaskDef = taskManager.read(taskId);
      if (oldTaskDef != null) {
        if (!taskManager.update(taskId, taskDefinition)) {
          oldTaskDef = null;
        }
      }
      return oldTaskDef;
    } catch (CrudsException ex) {
      throw new DataProcessorException(String.format("Error updating task definition: %s <-- %s", taskId, taskDefinition), ex);
    }
  }
}