package eu.wodrobina.rodos.retry.api;

import org.springframework.http.HttpStatusCode;

public record HttpGetResult(
        boolean success,
        HttpStatusCode statusCode,
        String errorMessage
) {
    public static HttpGetResult success(HttpStatusCode statusCode) {
        return new HttpGetResult(true, statusCode, null);
    }

    public static HttpGetResult failedStatus(HttpStatusCode statusCode) {
        return new HttpGetResult(false, statusCode, null);
    }

    public static HttpGetResult error(String errorMessage) {
        return new HttpGetResult(false, null, errorMessage);
    }
}
