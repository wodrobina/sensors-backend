package eu.wodrobina.rodos.web;


import eu.wodrobina.rodos.actuator.ActuatorService;
import eu.wodrobina.rodos.actuator.api.ActuatorResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ActuatorWebController {

    private final ActuatorService actuatorService;

    public ActuatorWebController(ActuatorService actuatorService) {
        this.actuatorService = actuatorService;
    }

    @GetMapping("/actuators")
    public String listActuators(Model model) {
        // Pobier...
        List<ActuatorResource> actuators = actuatorService.getAllActuators();
        // Przekazywanie...
        model.addAttribute("actuators", actuators);
        return "actuators-list"; // Nazwa pliku HTML w src/main/resources/templates/
    }


}
