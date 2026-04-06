package eu.wodrobina.rodos.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class ActuatorHttpService {

    private static final Logger logger = LoggerFactory.getLogger(ActuatorHttpService.class);

    private final RestTemplate restTemplate;

    public ActuatorHttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    void turnOn(Actuator actuator) {
        String url = actuator.buildOnUrl();
        call(url);
    }

    void turnOff(Actuator actuator) {
        String url = actuator.buildOffUrl();
        call(url);
    }

    private void call(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            logger.info("[HTTP] GET {} -> {}", url, response.getStatusCode());
        } catch (Exception e) {
            logger.warn("[HTTP] Error while calling - {}: {}", url, e.getMessage());
        }
    }
}
