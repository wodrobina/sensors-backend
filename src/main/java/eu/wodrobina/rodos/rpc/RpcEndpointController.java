package eu.wodrobina.rodos.rpc;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc")
public class RpcEndpointController {

    private final DispatchService dispatchService;

    public RpcEndpointController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping
    public ResponseEntity<JsonRpcResponse> rpc(@RequestBody JsonRpcRequest request) {
        Object id = request != null ? request.id() : null;

        try {
            if (request == null) {
                return ResponseEntity.ok(JsonRpcResponse.error(null, JsonRpcError.invalidRequest("Request body is null")));
            }
            if (!"2.0".equals(request.jsonrpc())) {
                return ResponseEntity.ok(JsonRpcResponse.error(id, JsonRpcError.invalidRequest("jsonrpc must be '2.0'")));
            }
            if (!StringUtils.hasText(request.method())) {
                return ResponseEntity.ok(JsonRpcResponse.error(id, JsonRpcError.invalidRequest("method is required")));
            }

            Object result = dispatchService.dispatch(request.method(), request.params());
            return ResponseEntity.ok(JsonRpcResponse.ok(id, result));

        } catch (JsonRpcException e) {
            return ResponseEntity.ok(JsonRpcResponse.error(id, e.error()));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonRpcResponse.error(id, JsonRpcError.internalError(e.getMessage())));
        }
    }

}
