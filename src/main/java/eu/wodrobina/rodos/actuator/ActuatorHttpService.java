package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.ActuatorResource;
import eu.wodrobina.rodos.retry.FailedHttpCallService;
import eu.wodrobina.rodos.retry.api.HttpGetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ActuatorHttpService {

    private static final Logger logger = LoggerFactory.getLogger(ActuatorHttpService.class);

    private final RestTemplate restTemplate;
    private final FailedHttpCallService failedHttpCallService;

    ActuatorHttpService(
            RestTemplate restTemplate,
            FailedHttpCallService failedHttpCallService
    ) {
        this.restTemplate = restTemplate;
        this.failedHttpCallService = failedHttpCallService;
    }

    public HttpGetResult executeGet(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            HttpStatusCode statusCode = response.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                logger.info("[HTTP] GET {} -> {}", url, statusCode);
                return HttpGetResult.success(statusCode);
            }

            logger.warn("[HTTP] GET {} returned status {}", url, statusCode);
            return HttpGetResult.failedStatus(statusCode);

        } catch (Exception e) {
            logger.warn("[HTTP] Error while calling {}: {}", url, e.getMessage());
            return HttpGetResult.error(e.getMessage());
        }
    }

    void turnOn(Actuator actuator) {
        executeAndStoreFailureIfNeeded(actuator.buildOnUrl());
    }

    void turnOff(Actuator actuator) {
        executeAndStoreFailureIfNeeded(actuator.buildOffUrl());
    }

    private void executeAndStoreFailureIfNeeded(String url) {
        HttpGetResult result = executeGet(url);

        if (!result.success()) {
            failedHttpCallService.saveIfNotExists(url);
        }
    }
}