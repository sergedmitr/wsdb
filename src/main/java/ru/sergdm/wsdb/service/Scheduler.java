package ru.sergdm.wsdb.service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class Scheduler {
	private final AtomicInteger testGauge;
	private final Counter testCounter;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Scheduler(MeterRegistry meterRegistry) {
		// Counter vs. gauge, summary vs. histogram
		// https://prometheus.io/docs/practices/instrumentation/#counter-vs-gauge-summary-vs-histogram
		testGauge = meterRegistry.gauge("custom_gauge", new AtomicInteger(0));
		testCounter = meterRegistry.counter("custom_counter");
		logger.info("Initialized!");
	}
	
	@Scheduled(fixedRateString = "1000", initialDelayString = "0")
	public void schedulingTask() {
		//logger.info("scheduled!");
		testGauge.set(Scheduler.getRandomNumberInRange(0, 100));
		testCounter.increment();
	}
	
	private static int getRandomNumberInRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
