package org.burgas.entityreflection.router;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.wallet.WalletRequest;
import org.burgas.entityreflection.exception.NotEnoughWalletBalanceException;
import org.burgas.entityreflection.exception.SameWalletException;
import org.burgas.entityreflection.exception.WalletNotFoundException;
import org.burgas.entityreflection.service.WalletService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class WalletRouter {

    private final WalletService walletService;

    @Bean
    public RouterFunction<ServerResponse> walletRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/wallets/by-identity", request -> ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.walletService.findWalletsByIdentity(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )

                )
                .GET(
                        "/api/v1/wallets/by-id", request -> ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.walletService.findById(
                                                        UUID.fromString(request.param("walletId").orElseThrow())
                                                )
                                        )
                )
                .POST(
                        "/api/v1/wallets/create-update", request -> {
                            UUID walletId = this.walletService.createOrUpdate(request.body(WalletRequest.class));
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/wallets/by-id?walletId=" + walletId))
                                    .body(walletId);
                        }
                )
                .DELETE(
                        "/api/v1/wallets/delete", request -> {
                            this.walletService.delete(
                                    UUID.fromString(request.param("walletId").orElseThrow())
                            );
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/wallets/deposit", request -> {
                            UUID depositOperationId = this.walletService.deposit(
                                    UUID.fromString(request.param("walletId").orElseThrow()),
                                    Double.parseDouble(request.param("amount").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/operations/by-id?operationId=" + depositOperationId))
                                    .body(depositOperationId);
                        }
                )
                .PUT(
                        "/api/v1/wallets/withdraw", request -> {
                            UUID withdrawOperationId = this.walletService.withdraw(
                                    UUID.fromString(request.param("walletId").orElseThrow()),
                                    Double.parseDouble(request.param("amount").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/operations/by-id?operationId=" + withdrawOperationId))
                                    .body(withdrawOperationId);
                        }
                )
                .PUT(
                        "/api/v1/wallets/transfer", request -> {
                            UUID transferOperationId = this.walletService.transfer(
                                    UUID.fromString(request.param("senderId").orElseThrow()),
                                    UUID.fromString(request.param("receiverId").orElseThrow()),
                                    Double.parseDouble(request.param("amount").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/operations/by-id?operationId=" + transferOperationId))
                                    .body(transferOperationId);
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
                        WalletNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        SameWalletException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        NotEnoughWalletBalanceException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
