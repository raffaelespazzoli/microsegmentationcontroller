package com.raffa.microsegmentationcontroller;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.models.V1Service;

public class SyncRequest {
	@SerializedName(value="parent")
	@JsonProperty("parent")
	private V1Service parent;

	public V1Service getService() {
		return parent;
	}

	public void setService(V1Service service) {
		this.parent = service;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
