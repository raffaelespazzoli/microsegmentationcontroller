package com.raffa.microsegmentationcontroller;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.models.V1ServiceStatus;
import io.kubernetes.client.models.V1beta1NetworkPolicy;

public class SyncResponse {
	@SerializedName(value="status")
	@JsonProperty("status")
	private V1ServiceStatus status=null;
	
	@SerializedName(value="children")
	@JsonProperty("children")
	private List<V1beta1NetworkPolicy> children;

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public V1ServiceStatus getStatus() {
		return status;
	}

	public void setStatus(V1ServiceStatus v1ServiceStatus) {
		this.status = v1ServiceStatus;
	}

	public List<V1beta1NetworkPolicy> getNps() {
		return children;
	}

	public void setNps(List<V1beta1NetworkPolicy> nps) {
		this.children = nps;
	}
	
}
