package org.sonar.plugins.dependencycheck;

/**
 * This class stores a License which is used in the plugin.
 */
public class License {
  private String id;
  private String title;
  private String description;
  private String url;
  private SourceType sourceType;
  private boolean commercial;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url == null ? "" : url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public void setSourceType(SourceType sourceType) {
    this.sourceType = sourceType;
  }

  public boolean isCommercial() {
    return commercial;
  }

  public void setCommercial(boolean commercial) {
    this.commercial = commercial;
  }
}
