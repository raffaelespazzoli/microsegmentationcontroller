package com.raffa.microsegmentationcontroller;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class Status {

	@SerializedName(value = "health")
	@JsonProperty("health")
	private String health;

	public Status(String string) {
		health=string;
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

	public String getStatus() {
		return health;
	}

	public void setStatus(String status) {
		this.health = status;
	}
}
