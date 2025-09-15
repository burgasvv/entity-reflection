package org.burgas.entityreflection.router;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.company.CompanyRequest;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.exception.CompanyNotFoundException;
import org.burgas.entityreflection.exception.EmptyEntityFieldException;
import org.burgas.entityreflection.service.CompanyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
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
public class CompanyRouter {

    private final CompanyService companyService;

    @Bean
    public RouterFunction<ServerResponse> companyRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/companies", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.companyService.findAll())
                )
                .GET(
                        "/api/v1/companies/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.companyService.findById(
                                                        UUID.fromString(
                                                                request.param("companyId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .POST(
                        "/api/v1/companies/create-update", request -> {
                            UUID companyId = this.companyService.createOrUpdate(
                                    request.body(CompanyRequest.class)
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/companies/by-id?companyId=" + companyId))
                                    .body(companyId);
                        }
                )
                .DELETE(
                        "/api/v1/companies/delete", request -> {
                            this.companyService.delete(
                                    UUID.fromString(
                                            request.param("companyId").orElseThrow()
                                    )
                            );
                            return ServerResponse.noContent().build();
                        }
                )
                .POST(
                        "/api/v1/companies/add-identity", request -> {
                            UUID companyId = this.companyService.addIdentity(
                                    UUID.fromString(request.param("companyId").orElseThrow()),
                                    request.body(Identity.class)
                            );
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/companies/by-id?companyId=" + companyId))
                                    .body(companyId);
                        }
                )
                .POST(
                        "/api/v1/companies/add-identities", request -> {
                            UUID companyId = this.companyService.addIdentities(
                                    UUID.fromString(request.param("companyId").orElseThrow()),
                                    request.body(new ParameterizedTypeReference<>(){})
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/companies/by-id?companyId=" + companyId))
                                    .body(companyId);
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
                        CompanyNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
