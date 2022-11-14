package com.solace.demo.amartens.dt.pubsub.event;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeEvent {
	public TimeEvent() {

	}
	public TimeEvent(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	private LocalDateTime timestamp;

	private String timezone;

	private long delay;

	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimezone() {
		return this.timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public long getDelay() {
		return this.delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public String toString() {
		return "{\"timestamp\":\""+this.timestamp+"\",\"timezone\":\""+this.timezone+"\",\"delay\":"+this.delay+"}";
	}
}
