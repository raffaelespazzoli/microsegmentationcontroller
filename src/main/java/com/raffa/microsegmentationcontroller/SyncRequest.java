package com.raffa.microsegmentationcontroller;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.models.V1Service;

public class SyncRequest {
	@SerializedName("parent")
	private V1Service service;

	public V1Service getService() {
		return service;
	}

	public void setService(V1Service service) {
		this.service = service;
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
