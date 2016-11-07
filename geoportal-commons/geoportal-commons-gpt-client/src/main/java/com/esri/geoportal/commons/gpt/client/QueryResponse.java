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
package com.esri.geoportal.commons.gpt.client;

import java.util.List;

/**
 * Query response.
 */
/*package*/ final class QueryResponse {
  public String _scroll_id;
  public Hits hits;
 
  public static final class Hits {
    public long total;
    public List<Hit> hits;
  }
  
  public static final class Hit {
    public String _id;
    public Source _source;
  }
  
  public static final class Source {
    public String src_uri_s;
    public String src_lastupdate_dt;
  }
}
