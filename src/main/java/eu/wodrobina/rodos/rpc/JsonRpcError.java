package eu.wodrobina.rodos.rpc;

public record JsonRpcError(int code, String message, Object data) {

    public static JsonRpcError invalidRequest(String msg) {
        return new JsonRpcError(-32600, msg, null);
    }

    public static JsonRpcError methodNotFound(String msg) {
        return new JsonRpcError(-32601, msg, null);
    }

    public static JsonRpcError invalidParams(String msg) {
        return new JsonRpcError(-32602, msg, null);
    }

    public static JsonRpcError internalError(String msg) {
        return new JsonRpcError(-32603, msg != null ? msg : "Internal error", null);
    }
}
