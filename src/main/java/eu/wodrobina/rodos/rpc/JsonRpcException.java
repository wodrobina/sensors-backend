package eu.wodrobina.rodos.rpc;

public class JsonRpcException extends RuntimeException {

    private final JsonRpcError error;

    JsonRpcException(JsonRpcError error) {
        super(error != null ? error.message() : null);
        this.error = error;
    }

    JsonRpcError error() {
        return error;
    }

}
