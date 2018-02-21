package com.raffa.microsegmentationcontroller;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.models.V1beta1NetworkPolicy;

public class Children {
	@SerializedName(value="NetworkPolicy.extensions/v1beta1")
	@JsonProperty("NetworkPolicy.extensions/v1beta1")
	private Map<String,V1beta1NetworkPolicy> networkpolicyMap;
	
	public Map<String, V1beta1NetworkPolicy> getNetworkpolicyMap() {
		return networkpolicyMap;
	}

	public void setNetworkpolicyMap(Map<String, V1beta1NetworkPolicy> networkpolicyMap) {
		this.networkpolicyMap = networkpolicyMap;
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
