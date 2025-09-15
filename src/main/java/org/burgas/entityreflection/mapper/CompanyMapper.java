package org.burgas.entityreflection.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.company.CompanyRequest;
import org.burgas.entityreflection.dto.company.CompanyResponseFull;
import org.burgas.entityreflection.dto.company.CompanyResponseShort;
import org.burgas.entityreflection.entity.company.Company;
import org.burgas.entityreflection.mapper.contract.EntityMapper;
import org.burgas.entityreflection.repository.CompanyRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.burgas.entityreflection.message.CompanyMessages.COMPANY_FIELD_DESCRIPTION_EMPTY;
import static org.burgas.entityreflection.message.CompanyMessages.COMPANY_FIELD_NAME_EMPTY;

@Component
@RequiredArgsConstructor
public final class CompanyMapper implements
        EntityMapper<UUID, CompanyRequest, Company, CompanyResponseFull, CompanyResponseShort> {

    private final CompanyRepository companyRepository;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    @Override
    public Company toEntity(CompanyRequest companyRequest) {
        UUID companyId = this.handleData(companyRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.companyRepository.findById(companyId)
                .map(
                        company -> {
                            String companyName = this.handleData(companyRequest.getName(), company.getName());
                            String companyDescription = this.handleData(companyRequest.getDescription(), company.getDescription());
                            return Company.builder()
                                    .id(company.getId())
                                    .name(companyName)
                                    .description(companyDescription)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String companyName = this.handleDataThrowable(
                                    companyRequest.getName(), COMPANY_FIELD_NAME_EMPTY.getMessage()
                            );
                            String companyDescription = this.handleDataThrowable(
                                    companyRequest.getDescription(), COMPANY_FIELD_DESCRIPTION_EMPTY.getMessage()
                            );
                            return Company.builder()
                                    .name(companyName)
                                    .description(companyDescription)
                                    .build();
                        }
                );
    }

    @Override
    public CompanyResponseFull toFullResponse(Company company) {
        CompanyResponseFull companyResponseFull = new CompanyResponseFull();
        companyResponseFull.setId(company.getId());
        companyResponseFull.setName(company.getName());
        companyResponseFull.setDescription(company.getDescription());
        companyResponseFull.setIdentities(
                company.getIdentities() == null ? null : company.getIdentities()
                        .stream()
                        .map(identity -> this.getIdentityMapper().toShortResponse(identity))
                        .toList()
        );
        return companyResponseFull;
    }

    @Override
    public CompanyResponseShort toShortResponse(Company company) {
        CompanyResponseShort companyResponseShort = new CompanyResponseShort();
        companyResponseShort.setId(company.getId());
        companyResponseShort.setName(company.getName());
        companyResponseShort.setDescription(company.getDescription());
        return companyResponseShort;
    }
}
