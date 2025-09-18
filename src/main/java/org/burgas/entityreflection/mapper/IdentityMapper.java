package org.burgas.entityreflection.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.identity.IdentityRequest;
import org.burgas.entityreflection.dto.identity.IdentityResponseFull;
import org.burgas.entityreflection.dto.identity.IdentityResponseShort;
import org.burgas.entityreflection.entity.company.Company;
import org.burgas.entityreflection.entity.identity.Authority;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.identity.IdentityFio;
import org.burgas.entityreflection.entity.identity.IdentitySecure;
import org.burgas.entityreflection.mapper.contract.EntityMapper;
import org.burgas.entityreflection.repository.CompanyRepository;
import org.burgas.entityreflection.repository.IdentityRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.burgas.entityreflection.message.IdentityMessages.*;

@Component
@RequiredArgsConstructor
public final class IdentityMapper implements
        EntityMapper<UUID, IdentityRequest, Identity, IdentityResponseFull, IdentityResponseShort> {

    private final IdentityRepository identityRepository;
    private final ObjectFactory<CompanyRepository> companyRepositoryObjectFactory;
    private final ObjectFactory<CompanyMapper> companyMapperObjectFactory;
    private final ObjectFactory<MachineMapper> machineMapperObjectFactory;
    private final PasswordEncoder passwordEncoder;

    private CompanyRepository getCompanyRepository() {
        return this.companyRepositoryObjectFactory.getObject();
    }

    private CompanyMapper getCompanyMapper() {
        return this.companyMapperObjectFactory.getObject();
    }

    private MachineMapper getMachineMapper() {
        return this.machineMapperObjectFactory.getObject();
    }

    @Override
    public Identity toEntity(IdentityRequest identityRequest) {
        UUID identityId = this.handleData(identityRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.identityRepository.findById(identityId)
                .map(
                        identity -> {
                            IdentitySecure identitySecure = this.handleData(
                                    identityRequest.getIdentitySecure(), identity.getIdentitySecure()
                            );
                            Authority authority = this.handleData(
                                    identitySecure.getAuthority(), identity.getIdentitySecure().getAuthority()
                            );
                            String username = this.handleData(
                                    identitySecure.getUsername(), identity.getIdentitySecure().getUsername()
                            );

                            IdentityFio identityFio = this.handleData(
                                    identityRequest.getIdentityFio(), identity.getIdentityFio()
                            );
                            String firstName = this.handleData(identityFio.getFirstname(), identity.getIdentityFio().getFirstname());
                            String lastName = this.handleData(identityFio.getLastname(), identity.getIdentityFio().getLastname());
                            String patronymic = this.handleData(identityFio.getPatronymic(), identity.getIdentityFio().getPatronymic());

                            UUID companyId = this.handleData(identityRequest.getCompanyId(),
                                    UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
                            Company company = this.handleData(
                                    getCompanyRepository().findById(companyId).orElse(null),
                                    identity.getCompany()
                            );

                            return Identity.builder()
                                    .id(identity.getId())
                                    .identitySecure(
                                            IdentitySecure.builder()
                                                    .authority(authority)
                                                    .username(username)
                                                    .password(identity.getPassword())
                                                    .build()
                                    )
                                    .identityFio(
                                            IdentityFio.builder()
                                                    .firstname(firstName)
                                                    .lastname(lastName)
                                                    .patronymic(patronymic)
                                                    .build()
                                    )
                                    .company(company)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            IdentitySecure identitySecure = this.handleDataThrowable(
                                    identityRequest.getIdentitySecure(), IDENTITY_SECURE_OBJECT_EMPTY.getMessage()
                            );
                            Authority authority = this.handleDataThrowable(
                                    identitySecure.getAuthority(), IDENTITY_AUTHORITY_FIELD_EMPTY.getMessage()
                            );
                            String username = this.handleDataThrowable(
                                    identitySecure.getUsername(), IDENTITY_USERNAME_FIELD_EMPTY.getMessage()
                            );
                            String password = this.handleDataThrowable(
                                    identitySecure.getPassword(), IDENTITY_PASSWORD_FIELD_EMPTY.getMessage()
                            );

                            IdentityFio identityFio = this.handleDataThrowable(
                                    identityRequest.getIdentityFio(), IDENTITY_FIO_OBJECT_EMPTY.getMessage()
                            );
                            String firstName = this.handleDataThrowable(
                                    identityFio.getFirstname(), IDENTITY_FIRSTNAME_FIELD_EMPTY.getMessage()
                            );
                            String lastName = this.handleDataThrowable(
                                    identityFio.getLastname(), IDENTITY_LASTNAME_FIELD_EMPTY.getMessage()
                            );
                            String patronymic = this.handleDataThrowable(
                                    identityFio.getPatronymic(), IDENTITY_PATRONYMIC_FIELD_EMPTY.getMessage()
                            );

                            UUID companyId = this.handleData(identityRequest.getCompanyId(),
                                    UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
                            Company company = this.handleDataThrowable(
                                    getCompanyRepository().findById(companyId).orElse(null),
                                    IDENTITY_COMPANY_FIELD_EMPTY.getMessage()
                            );

                            return Identity.builder()
                                    .identitySecure(
                                            IdentitySecure.builder()
                                                    .authority(authority)
                                                    .username(username)
                                                    .password(this.passwordEncoder.encode(password))
                                                    .build()
                                    )
                                    .identityFio(
                                            IdentityFio.builder()
                                                    .firstname(firstName)
                                                    .lastname(lastName)
                                                    .patronymic(patronymic)
                                                    .build()
                                    )
                                    .company(company)
                                    .build();
                        }
                );
    }

    @Override
    public IdentityResponseFull toFullResponse(Identity identity) {
        IdentityResponseFull identityResponseFull = new IdentityResponseFull();
        identityResponseFull.setId(identity.getId());
        identityResponseFull.setIdentitySecure(identity.getIdentitySecure());
        identityResponseFull.setIdentityFio(identity.getIdentityFio());
        if (identity.getCompany() == null) {
            identityResponseFull.setCompany(null);

        } else {
            identityResponseFull.setCompany(getCompanyMapper().toShortResponse(identity.getCompany()));
        }
        identityResponseFull.setMachines(
                identity.getMachines() == null ? null : identity.getMachines()
                        .stream()
                        .map(machine -> this.getMachineMapper().toShortResponse(machine))
                        .toList()
        );
        return identityResponseFull;
    }

    @Override
    public IdentityResponseShort toShortResponse(Identity identity) {
        IdentityResponseShort identityResponseShort = new IdentityResponseShort();
        identityResponseShort.setId(identity.getId());
        identityResponseShort.setIdentitySecure(identity.getIdentitySecure());
        identityResponseShort.setIdentityFio(identity.getIdentityFio());
        return identityResponseShort;
    }
}
