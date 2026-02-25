package eu.wodrobina.rodos.sensorreading.api;


import eu.wodrobina.rodos.sensor.SensorService;
import eu.wodrobina.rodos.sensorreading.TestUtils;
import eu.wodrobina.rodos.sensorreading.TruncateTablesExtension;
import org.hamcrest.Matcher;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SensorControllerWebMvcTest {

    @Autowired
    @RegisterExtension
    TruncateTablesExtension truncateTablesExtension;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SensorService sensorService;

    String requestJson;

    ResultActions resultActions;

    @Test
    void should_register_new_sensor() throws Exception {
        givenSensorRegisterRequest("mySensor", null, TestUtils.generateTestPublicKeyBase64());

        whenRequestIsPerformed();

        resultActions.andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").value(isUUID()))
                .andExpect(jsonPath("$.result.sensorName").value("mySensor"))
                .andExpect(jsonPath("$.result.sensorComment").value("null"))
                .andExpect(jsonPath("$.result.publicKey").exists());
    }

    @Test
    void should_fail_to_register_multiple_times() throws Exception {
        final String publicKeyBase64 = TestUtils.generateTestPublicKeyBase64();
        givenRegisteredSensor(publicKeyBase64);
        givenSensorRegisterRequest("otherSensor", null, publicKeyBase64);

        whenRequestIsPerformed();

        thenRequestIsOkWithError();
    }

    private void whenRequestIsPerformed() throws Exception {
        resultActions = mockMvc.perform(post("/rpc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
    }

    private void givenSensorRegisterRequest(String sensorName, String sensorComment, String sensorPublicKey) {
        requestJson = String.format(
                """
                        {
                          "jsonrpc": "2.0",
                          "id": 1,
                          "method": "sensor.register",
                          "params": {
                            "sensorName": "%s",
                            "sensorComment": "%s",
                            "publicKey": "%s"
                          }
                        }
                        """, sensorName, sensorComment, sensorPublicKey
        );
    }

    private void givenRegisteredSensor(String publicKeyBase64) throws Exception {
        givenSensorRegisterRequest("mySensor", null, publicKeyBase64);
        whenRequestIsPerformed();
    }

    private void thenRequestIsOkWithError() throws Exception {
        resultActions.andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.error").exists());
    }

    private static @NonNull Matcher<String> isUUID() {
        return org.hamcrest.Matchers.matchesPattern(
                "^[0-9a-fA-F\\-]{36}$"
        );
    }

}