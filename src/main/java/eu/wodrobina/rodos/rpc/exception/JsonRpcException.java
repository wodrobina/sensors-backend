package eu.wodrobina.rodos.rpc.exception;

import eu.wodrobina.rodos.rpc.api.JsonRpcError;

public class JsonRpcException extends RuntimeException {

    private final JsonRpcError error;

    public JsonRpcException(JsonRpcError error) {
        super(error != null ? error.message() : null);
        this.error = error;
    }

    public JsonRpcError error() {
        return error;
    }

}
