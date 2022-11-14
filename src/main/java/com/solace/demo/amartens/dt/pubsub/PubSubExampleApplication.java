package com.solace.demo.amartens.dt.pubsub;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.solace.demo.amartens.dt.pubsub.event.TimeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.StaticMessageHeaderAccessor;
import org.springframework.integration.acks.AckUtils;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
public class PubSubExampleApplication {
	private static final Logger logger = LoggerFactory.getLogger(PubSubExampleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PubSubExampleApplication.class, args);
	}

	@PollableBean
	public Supplier<Message<TimeEvent>> timeEventSupplier() {
		return () -> {
			TimeEvent event = new TimeEvent(LocalDateTime.now(ZoneId.systemDefault()));
			Message<TimeEvent> timeEvent = MessageBuilder.withPayload(event)
					.setHeader("sap.businessUnit", "unit1")
					.setHeader("sap.businessProcess", "process1")
					.setHeader("solace.function", "timeEventSupplier")
					//.setHeader(BinderHeaders.TARGET_DESTINATION,  "new-target-destination")
					.build();
			return timeEvent;
		};
	}

	@Bean
	public Function<Message<TimeEvent>,Message<TimeEvent>> timeEventUpdater() {
		return message -> {
			AcknowledgmentCallback acknowledgmentCallback = StaticMessageHeaderAccessor.getAcknowledgmentCallback(message);
			acknowledgmentCallback.noAutoAck();

			TimeEvent event = message.getPayload();
			event.setTimezone(ZoneId.systemDefault().toString());

			AckUtils.accept(acknowledgmentCallback);

			Message<TimeEvent> updatedTimeEvent = MessageBuilder.withPayload(event)
					.setHeader("sap.businessUnit", "unit1")
					.setHeader("sap.businessProcess", "process2")
					.setHeader("solace.function", "timeEventUpdater")
					.build();
			return updatedTimeEvent;
		};
	}

	@Bean
	public Function<Message<TimeEvent>,Message<TimeEvent>> timeEventConverter() {
		return message -> {
			AcknowledgmentCallback acknowledgmentCallback = StaticMessageHeaderAccessor.getAcknowledgmentCallback(message);
			acknowledgmentCallback.noAutoAck();

			TimeEvent event = message.getPayload();

			LocalDateTime lDT = event.getTimestamp();
			ZonedDateTime zDT = lDT.atZone(ZoneId.of(event.getTimezone())).minusHours(1);

			event.setTimestamp(zDT.withZoneSameLocal(ZoneId.of("UTC")).toLocalDateTime());
			event.setTimezone("UTC");
			event.setDelay(Duration.between(lDT, LocalDateTime.now()).toMillis());

			AckUtils.accept(acknowledgmentCallback);

			Message<TimeEvent> convertedTimeEvent = MessageBuilder.withPayload(event)
					.setHeader("sap.businessUnit", "unit1")
					.setHeader("sap.businessProcess", "process3")
					.setHeader("solace.function", "timeEventConverter")
					.build();
			return convertedTimeEvent;
		};
	}

	@Bean
	public Consumer<Message<TimeEvent>> timeEventConsumer() {
		return message -> {
			TimeEvent event = message.getPayload();
			logger.info("Time event received: " + event.toString());
		};
	}
}
