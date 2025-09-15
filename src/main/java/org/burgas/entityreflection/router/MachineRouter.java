package org.burgas.entityreflection.router;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.machine.MachineRequest;
import org.burgas.entityreflection.exception.EmptyEntityFieldException;
import org.burgas.entityreflection.exception.MachineNotFoundException;
import org.burgas.entityreflection.service.MachineService;
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
public class MachineRouter {

    private final MachineService machineService;

    @Bean
    public RouterFunction<ServerResponse> machineRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/machines", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.machineService.findAll())
                )
                .GET(
                        "/api/v1/machines/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.machineService.findById(
                                                        UUID.fromString(
                                                                request.param("machineId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .POST(
                        "/api/v1/machines/create-update", request -> {
                            UUID machineId = this.machineService.createOrUpdate(
                                    request.body(MachineRequest.class)
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/machines/by-id?machineId=" + machineId))
                                    .body(machineId);
                        }
                )
                .DELETE(
                        "/api/v1/machines/delete", request -> {
                            this.machineService.delete(
                                    UUID.fromString(
                                            request.param("machineId").orElseThrow()
                                    )
                            );
                            return ServerResponse.noContent().build();
                        }
                )
                .POST(
                        "/api/v1/machines/add-identity", request -> {
                            UUID machineId = this.machineService.addIdentity(
                                    UUID.fromString(request.param("machineId").orElseThrow()),
                                    UUID.fromString(request.param("identityId").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/machines/by-id?machineId=" + machineId))
                                    .body(machineId);
                        }
                )
                .POST(
                        "/api/v1/machines/add-identities", request -> {
                            List<UUID> identityIds = Arrays.stream(request.servletRequest().getParameterValues("identityId"))
                                    .map(UUID::fromString)
                                    .toList();
                            UUID machineId = this.machineService.addIdentities(
                                    UUID.fromString(request.param("machineId").orElseThrow()),
                                    identityIds
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/machines/by-id?machineId=" + machineId))
                                    .body(machineId);
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
                        MachineNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
