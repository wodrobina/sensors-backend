package eu.wodrobina.rodos.retry;

import eu.wodrobina.rodos.actuator.ActuatorHttpService;
import eu.wodrobina.rodos.retry.api.HttpGetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class FailedHttpCallScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FailedHttpCallScheduler.class);

    private final FailedHttpCallService repository;
    private final ActuatorHttpService actuatorHttpService;

    FailedHttpCallScheduler(
            FailedHttpCallService repository,
            ActuatorHttpService actuatorHttpService
    ) {
        this.repository = repository;
        this.actuatorHttpService = actuatorHttpService;
    }

    @Scheduled(fixedDelayString = "${actuator.retry.fixed-delay-ms:30000}")
    void retryFailedCalls() {
        List<FailedHttpCall> failedCalls = repository.findAll();

        for (FailedHttpCall failedCall : failedCalls) {
            repository.markAttempt(failedCall.getId());

            HttpGetResult httpGetResult = actuatorHttpService.executeGet(failedCall.getUrl());

            if (httpGetResult.success()) {
                repository.deleteById(failedCall.getId());

                logger.info(
                        "[HTTP RETRY] Successfully retried and removed {}",
                        failedCall.getUrl()
                );
            } else {
                logger.warn(
                        "[HTTP RETRY] Still failing {}, attempts: {}",
                        failedCall.getUrl(),
                        failedCall.getAttempts() + 1
                );
            }
        }
    }
}