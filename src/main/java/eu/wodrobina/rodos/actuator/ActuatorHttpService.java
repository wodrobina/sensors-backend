package eu.wodrobina.rodos.actuator;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ActuatorHttpService {

    private final RestTemplate restTemplate;

    public ActuatorHttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void turnOn(Actuator actuator) {
        String url = actuator.buildOnUrl();
        call(url);
    }

    public void turnOff(Actuator actuator) {
        String url = actuator.buildOffUrl();
        call(url);
    }

    private void call(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("[HTTP] GET " + url + " -> " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("[HTTP] Błąd wywołania " + url + ": " + e.getMessage());
        }
    }
}
