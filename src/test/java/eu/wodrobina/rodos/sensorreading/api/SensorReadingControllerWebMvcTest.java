package eu.wodrobina.rodos.sensorreading.api;


import eu.wodrobina.rodos.sensorreading.SensorReading;
import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.SensorUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SensorReadingControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SensorReadingService sensorReadingService;

    String requestJson;

    @Test
    void should_return_all_readings_by_sensor_name() throws Exception {
        givenReadings("otherSensor");
        givenReadings("mySensor");
        givenFindAllBySensorNameRequest("mySensor");

        mockMvc.perform(post("/rpc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").isNumber())
                .andExpect(jsonPath("$.result[0].sensorName").value("mySensor"));
    }

    @Test
    void should_create_single_reading() throws Exception {
        String requestJson = """
                {
                  "jsonrpc": "2.0",
                  "id": 2,
                  "method": "sensor.reading.create",
                  "params": {
                    "sensorName": "exampleSensor",
                    "reading": 25.0,
                    "unit": "CELSIUS"
                  }
                }
                """;

        mockMvc.perform(post("/rpc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.jsonrpc")
                        .value("2.0"))
                .andExpect(jsonPath("$.id")
                        .value(2))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.result.id").isNumber())
                .andExpect(jsonPath("$.result.sensorName").value("exampleSensor"))
                .andExpect(jsonPath("$.result.unit").value("°C"));

        assertThat(sensorReadingService.findAllByName("exampleSensor")).hasSize(1);
        assertThat(sensorReadingService.findAllByName("exampleSensor").iterator().next().sensorName()).isEqualTo("exampleSensor");
    }

    private void givenFindAllBySensorNameRequest(String sensorName) {
        requestJson = String.format(
                """
                {
                  "jsonrpc": "2.0",
                  "id": 1,
                  "method": "sensor.reading.findAllByName",
                  "params": {
                    "sensorName": "%s"
                  }
                }
                """, sensorName
        );
    }

    private void givenReadings(String sensorName) {
        sensorReadingService.save(SensorReading.of(sensorName, new BigDecimal("25.0"), SensorUnit.CELSIUS));
        sensorReadingService.save(SensorReading.of(sensorName, new BigDecimal("26.0"), SensorUnit.CELSIUS));
    }

    @Test
    void should_return_method_not_found_error() throws Exception {
        String requestJson = """
                {
                  "jsonrpc": "2.0",
                  "id": 123,
                  "method": "no.such.method",
                  "params": {}
                }
                """;

        mockMvc.perform(post("/rpc").contentType(MediaType.APPLICATION_JSON).content(requestJson)).andExpect(status().isOk()).andExpect(jsonPath("$.jsonrpc").value("2.0")).andExpect(jsonPath("$.id").value(123)).andExpect(jsonPath("$.result").doesNotExist()).andExpect(jsonPath("$.error.code").value(-32601));
    }
}