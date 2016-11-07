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
package com.esri.geoportal.commons.robots;

import com.esri.geoportal.commons.constants.HttpConstants;

/**
 * Bots config.
 */
public interface BotsConfig {
  /**
   * Gets user agent.
   * @return user agent
   */
  String getUserAgent();
  
  /**
   * Checks if bots is enabled.
   * @return <code>true</code> if bots are enabled
   */
  boolean isEnabled();
  
  /**
   * Checks if bots can be override.
   * @return <code>true</code> if bots can be override
   */
  boolean isOverride();
  
  /**
   * Default bots configuration.
   */
  BotsConfig DEFAULT = new BotsConfigImpl(HttpConstants.getUserAgent(), true, true);
}
