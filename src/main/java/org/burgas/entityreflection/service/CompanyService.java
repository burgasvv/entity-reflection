package org.burgas.entityreflection.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.company.CompanyRequest;
import org.burgas.entityreflection.dto.company.CompanyResponseFull;
import org.burgas.entityreflection.dto.company.CompanyResponseShort;
import org.burgas.entityreflection.entity.company.Company;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.exception.CompanyNotFoundException;
import org.burgas.entityreflection.mapper.CompanyMapper;
import org.burgas.entityreflection.message.CompanyMessages;
import org.burgas.entityreflection.repository.CompanyRepository;
import org.burgas.entityreflection.service.contract.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class CompanyService implements CrudService<UUID, CompanyRequest, CompanyResponseFull, CompanyResponseShort> {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public Company findCompany(final UUID companyId) {
        return this.companyRepository.findById(
                        companyId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : companyId
                )
                .orElseThrow(
                        () -> new CompanyNotFoundException(CompanyMessages.COMPANY_NOT_FOUND.getMessage())
                );
    }

    @Override
    public List<CompanyResponseShort> findAll() {
        return this.companyRepository.findAll()
                .stream()
                .map(this.companyMapper::toShortResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseFull findById(UUID uuid) {
        return this.companyMapper.toFullResponse(this.findCompany(uuid));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(CompanyRequest companyRequest) {
        Company entity = this.companyMapper.toEntity(companyRequest);
        return this.companyRepository.save(entity).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(UUID uuid) {
        Company company = this.findCompany(uuid);
        this.companyRepository.delete(company);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID addIdentity(final UUID companyId, final Identity identity) {
        Company company = this.findCompany(companyId);
        company.addIdentity(identity);
        return company.getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID addIdentities(final UUID companyId, final List<Identity> identities) {
        Company company = this.findCompany(companyId);
        company.addIdentities(identities);
        return company.getId();
    }
}
