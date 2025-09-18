package org.burgas.entityreflection.router;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.identity.IdentityRequest;
import org.burgas.entityreflection.exception.EmptyEntityFieldException;
import org.burgas.entityreflection.exception.IdentityNotFoundException;
import org.burgas.entityreflection.service.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class IdentityRouter {

    private final IdentityService identityService;

    @Bean
    public RouterFunction<ServerResponse> identityRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/identities", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.identityService.findAll())
                )
                .GET(
                        "/api/v1/identities/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.identityService.findById(
                                                        UUID.fromString(
                                                                request.param("identityId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .POST(
                        "/api/v1/identities/create", request -> {
                            UUID identityId = this.identityService.createOrUpdate(
                                    request.body(IdentityRequest.class)
                            );
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(identityId);
                        }
                )
                .PUT(
                        "/api/v1/identities/update", request -> {
                            UUID identityId = this.identityService.createOrUpdate(
                                    request.body(IdentityRequest.class)
                            );
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(identityId);
                        }
                )
                .DELETE(
                        "/api/v1/identities/delete", request -> {
                            this.identityService.delete(
                                    UUID.fromString(request.param("identityId").orElseThrow())
                            );
                            return ServerResponse.noContent().build();
                        }
                )
                .POST(
                        "/api/v1/identities/add-machine", request -> {
                            UUID identityId = this.identityService.addMachine(
                                    UUID.fromString(request.param("identityId").orElseThrow()),
                                    UUID.fromString(request.param("machineId").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/identities/by-id?identityId=" + identityId))
                                    .body(identityId);
                        }
                )
                .POST(
                        "/api/v1/identities/add-machines", request -> {
                            List<UUID> machineIds = Arrays.stream(request.servletRequest().getParameterValues("machineId"))
                                    .map(UUID::fromString)
                                    .toList();
                            UUID identityId = this.identityService.addMachines(
                                    UUID.fromString(request.param("identityId").orElseThrow()),
                                    machineIds
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/identities/by-id?identityId=" + identityId))
                                    .body(identityId);
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
                        IdentityNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
