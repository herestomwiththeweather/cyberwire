package org.opensourcecurrency.hack;

public class Asset {
	public Integer assetId;
	public String url;
	public String name;
	
	public Integer m_providerId;
	private Provider m_provider;
	
	public void setId(Integer id) {
		assetId = id;
	}
	
	public void setUrl(String asset_url) {
		url = asset_url;
	}
	
	public void setName(String asset_name) {
		name = asset_name;
	}
	
	public void setProviderId(Integer provider_id) {
		m_providerId = provider_id;
	}
	
	public void setProvider(Provider provider) {
		m_provider = provider;
	}
	
	public Provider getProvider() {
		return m_provider;
	}
}
