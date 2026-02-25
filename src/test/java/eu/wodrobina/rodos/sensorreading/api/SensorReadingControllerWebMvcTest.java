package eu.wodrobina.rodos.sensorreading.api;


import api.SensorResource;
import eu.wodrobina.rodos.sensor.SensorService;
import eu.wodrobina.rodos.sensor.api.RegisterSensorRequest;
import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.SensorUnit;
import eu.wodrobina.rodos.sensorreading.TestUtils;
import eu.wodrobina.rodos.sensorreading.TruncateTablesExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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
    @RegisterExtension
    TruncateTablesExtension truncateTablesExtension;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SensorReadingService sensorReadingService;

    @Autowired
    SensorService sensorService;

    String requestJson;
    SensorResource sensor;

    @Test
    void should_return_all_readings_by_sensor_name() throws Exception {
        givenExistingSensor();
        givenTemperatureReadings(25.0);
        givenTemperatureReadings(26.0);
        givenFindAllBySensorNameRequest();

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
                .andExpect(jsonPath("$.result[0].sensorId").value(sensor.id().toString()));
    }

    @Test
    void should_create_single_reading() throws Exception {
        givenExistingSensor();
        givenSensorReadingRequest();

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
                .andExpect(jsonPath("$.result.sensorId").value(sensor.id().toString()))
                .andExpect(jsonPath("$.result.unit").value("°C"))
                .andExpect(jsonPath("$.result.createdAt").isNotEmpty());

        assertThat(sensorReadingService.findAllByReadingForSensor(sensor.id())).hasSize(1);
    }

    private void givenExistingSensor() {
        sensor = sensorService.registerSensor(RegisterSensorRequest.fromRequestParams(Map.of(
                "sensorName", "DS18B20",
                "sensorComment", "my example Temperature sensor ",
                "publicKey", TestUtils.generateTestPublicKeyBase64()
        )));
    }

    private void givenSensorReadingRequest() {
        assertThat(sensor)
                .withFailMessage("Test configuration: setup sensor first.")
                .isNotNull();

        requestJson = String.format(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 2,
                          "method": "sensor.reading.create",
                          "params": {
                            "sensorId": "%s",
                            "reading": "25.0",
                            "unit": "CELSIUS"
                          }
                        }
                        """, sensor.id()
        );
    }

    private void givenFindAllBySensorNameRequest() {
        requestJson = String.format(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "method": "sensor.reading.findAllByName",
                          "params": {
                            "sensorId": "%s"
                          }
                        }
                        """, sensor.id()
        );
    }

    private void givenTemperatureReadings(Double value) {
        sensorReadingService.save(SensorReadingRequest.of(sensor.id(), new BigDecimal(value), SensorUnit.CELSIUS));
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