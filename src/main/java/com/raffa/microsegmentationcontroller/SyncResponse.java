package com.raffa.microsegmentationcontroller;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.models.V1beta1NetworkPolicy;

public class SyncResponse {
	@SerializedName("status")
	private String status=null;
	
	@SerializedName("children")
	private List<V1beta1NetworkPolicy> nps;

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<V1beta1NetworkPolicy> getNps() {
		return nps;
	}

	public void setNps(List<V1beta1NetworkPolicy> nps) {
		this.nps = nps;
	}
	
}
