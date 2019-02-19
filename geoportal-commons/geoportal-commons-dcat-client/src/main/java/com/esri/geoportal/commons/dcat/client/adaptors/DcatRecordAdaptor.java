/*
 * Copyright 2019 Esri.
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
package com.esri.geoportal.commons.dcat.client.adaptors;

import com.esri.geoportal.commons.dcat.client.dcat.DcatContactPoint;
import com.esri.geoportal.commons.dcat.client.dcat.DcatDistributionList;
import com.esri.geoportal.commons.dcat.client.dcat.DcatPublisher;
import com.esri.geoportal.commons.dcat.client.dcat.DcatRecord;
import com.esri.geoportal.commons.dcat.client.json.JsonAttribute;
import com.esri.geoportal.commons.dcat.client.json.JsonRecord;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import ucar.nc2.grib.TimeCoordUnion.Val;

/**
 * DCAT record adaptor.
 */
public class DcatRecordAdaptor extends DcatAdaptor implements DcatRecord {
  private final JsonRecord record;

  /**
   * Creates instance of the adaptor.
   * @param record raw record
   */
  public DcatRecordAdaptor(JsonRecord record) {
    super(record);
    this.record = record;
  }

  @Override
  public String getTitle() {
    return getString("title");
  }

  @Override
  public String getDescription() {
    return StringUtils.defaultIfBlank(getString("description"),getAbstract());
  }

  @Override
  public String getAbstract() {
    return StringUtils.trimToEmpty(getString("abstract"));
  }

  @Override
  public List<String> getKeywords() {
    ArrayList<String> keywords = new ArrayList<String>();
    for (JsonAttribute keyword: record.getKeywords()) {
      keywords.add(keyword.getString());
    }
    return keywords;
  }
  
  @Override
  public List<String> getThemes() {
    ArrayList<String> themes = new ArrayList<String>();
    for (JsonAttribute theme: record.getThemes()) {
      themes.add(theme.getString());
    }
    return themes;
  }

  @Override
  public String getIdentifier() {
    return getString("identifier");
  }

  @Override
  public String getAccessLevel() {
    return getString("accessLevel");
  }

  @Override
  public String getSpatial() {
    return getString("spatial");
  }

  @Override
  public String getTemporal() {
    return getString("temporal");
  }

  @Override
  public String getModified() {
    return getString("modified");
  }

  @Override
  public DcatPublisher getPublisher() {
	    return new DcatPublisherAdaptor(record);
  }

  @Override
  public String getLicense() {
    return getString("license");
  }

  @Override
  public DcatDistributionList getDistribution() {
    return new DcatDistributionListAdaptor(record.getDistribution());
  }
  
  @Override
  public String toString() {
    return record.toString();
  }

  @Override
  public String getType() {
    return getString("@type");
  }

  @Override
  public DcatContactPoint getContactPoint() {
    return new DcatContactPointAdaptor(record);
  }

  @Override
  public List<String> getBureauCodes() {
    ArrayList<String> codes = new ArrayList<String>();
    for (JsonAttribute jsonCcode: record.getBureauCodes()) {
      String code = jsonCcode.getString();
      codes.add(code);
    }
    return codes;
  }

  @Override
  public List<String> getProgramCodes() {
    ArrayList<String> codes = new ArrayList<String>();
    for (JsonAttribute jsonCcode: record.getProgramCodes()) {
      String code = jsonCcode.getString();
      codes.add(code);
    }
    return codes;
  }

  @Override
  public String getRights() {
    return getString("rights");
  }

  @Override
  public String getConformsTo() {
    return getString("conformsTo");
  }

  @Override
  public String getDataQuality() {
    return getString("dataQuality");
  }

  @Override
  public String getDescribedBy() {
    return getString("describedBy");
  }

  @Override
  public String getDescribedByType() {
    return getString("describedByType");
  }

  @Override
  public String isPartOf() {
    return getString("isPartOf");
  }

  @Override
  public String getIssued() {
    return getString("issued");
  }

  @Override
  public List<String> getLanguages() {
    ArrayList<String> languages = new ArrayList<String>();
    for (JsonAttribute reference: record.getLanguages()) {
      languages.add(reference.getString());
    }
    return languages;
  }

  @Override
  public String getLandingPage() {
    return getString("landingPage");
  }

  @Override
  public String getPrimaryITInvestmentUII() {
    return getString("primaryITInvestmentUII");
  }

  @Override
  public List<String> getReferences() {
    ArrayList<String> references = new ArrayList<String>();
    for (JsonAttribute reference: record.getReferences()) {
      references.add(reference.getString());
    }
    return references;
  }

  @Override
  public List<String> getSystemRecords() {
    ArrayList<String> systemrecords = new ArrayList<String>();
    for (JsonAttribute systemrecord: record.getSystemRecords()) {
      systemrecords.add(systemrecord.getString());
    }
    return systemrecords;
  }

  @Override
  public String getAccrualPeriodicity() {
    return getString("accrualPeriodicity");
  }

  @Override
  public String getTheme() {
    return getString("theme");
  }

  @Override
  public String getIsPartOf() {
    return getString("isPartOf");
  }
  
  
}
