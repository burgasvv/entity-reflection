package org.burgas.entityreflection.router;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.exception.EmptyEntityFieldException;
import org.burgas.entityreflection.exception.OperationNotFoundException;
import org.burgas.entityreflection.service.OperationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class OperationRouter {

    private final OperationService operationService;

    @Bean
    public RouterFunction<ServerResponse> operationRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/operations/by-sender-wallet", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.operationService.findBySenderWalletId(
                                                UUID.fromString(request.param("senderWalletId").orElseThrow())
                                        )
                                )
                )
                .GET(
                        "/api/v1/operations/by-receiver-wallet", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.operationService.findByReceiverWalletId(
                                                UUID.fromString(request.param("receiverWalletId").orElseThrow())
                                        )
                                )
                )
                .GET(
                        "/api/v1/operations/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.operationService.findById(
                                                UUID.fromString(request.param("operationId").orElseThrow())
                                        )
                                )
                )
                .DELETE(
                        "/api/v1/operations/delete", request -> {
                            this.operationService.delete(
                                    UUID.fromString(request.param("operationId").orElseThrow())
                            );
                            return ServerResponse.noContent().build();
                        }
                )
                .onError(
                        DataIntegrityViolationException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        EmptyEntityFieldException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        OperationNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
