package org.coody.framework.tester;

public class SettingInfo {

	// Fields

	private Integer id;
	private String siteName;
	private String keywords;
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SettingInfo() {
	}

	@Override
	public String toString() {
		return "SettingInfo [id=" + id + ", siteName=" + siteName + ", keywords=" + keywords + ", description="
				+ description + "]";
	}

}