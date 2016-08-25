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
package com.esri.geoportal.commons.meta;

import org.w3c.dom.Document;

/**
 * Metadata handler.
 */
public interface MetaHandler {
  /**
   * Creates document based on properties.
   * @param wellKnowsAttributes attributes
   * @return document
   * @throws MetaException if metadata creation failed
   */
  Document create(ObjectAttribute wellKnowsAttributes) throws MetaException;
  
  /**
   * Extracts attributes from the document.
   * @param doc document.
   * @return attributes
   * @throws MetaException if metadata extraction failed
   */
  ObjectAttribute extract(Document doc) throws MetaException;
}
