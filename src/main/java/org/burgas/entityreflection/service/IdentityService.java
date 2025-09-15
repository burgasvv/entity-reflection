package org.burgas.entityreflection.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.identity.IdentityRequest;
import org.burgas.entityreflection.dto.identity.IdentityResponseFull;
import org.burgas.entityreflection.dto.identity.IdentityResponseShort;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.machine.Machine;
import org.burgas.entityreflection.exception.IdentityNotFoundException;
import org.burgas.entityreflection.mapper.IdentityMapper;
import org.burgas.entityreflection.message.IdentityMessages;
import org.burgas.entityreflection.repository.IdentityRepository;
import org.burgas.entityreflection.service.contract.CrudService;
import org.springframework.beans.factory.ObjectFactory;
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
public class IdentityService implements CrudService<UUID, IdentityRequest, IdentityResponseFull, IdentityResponseShort> {

    private final IdentityRepository identityRepository;
    private final IdentityMapper identityMapper;
    private final ObjectFactory<MachineService> machineServiceObjectFactory;

    private MachineService getMachineService() {
        return this.machineServiceObjectFactory.getObject();
    }

    public Identity findIdentity(final UUID identityId) {
        return this.identityRepository.findById(
                        identityId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : identityId
                )
                .orElseThrow(
                        () -> new IdentityNotFoundException(IdentityMessages.IDENTITY_NOT_FOUND.getMessage())
                );
    }

    @Override
    public List<IdentityResponseShort> findAll() {
        return this.identityRepository.findAll()
                .stream()
                .map(this.identityMapper::toShortResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IdentityResponseFull findById(UUID uuid) {
        return this.identityMapper.toFullResponse(this.findIdentity(uuid));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(IdentityRequest identityRequest) {
        Identity entity = this.identityMapper.toEntity(identityRequest);
        return this.identityRepository.save(entity).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(UUID uuid) {
        Identity identity = this.findIdentity(uuid);
        this.identityRepository.delete(identity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID addMachine(final UUID identityId, final UUID machineId) {
        Identity identity = this.findIdentity(identityId);
        Machine machine = getMachineService().findMachine(machineId);
        identity.addMachine(machine);
        return identity.getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID addMachines(final UUID identityId, final List<UUID> machineIds) {
        Identity identity = this.findIdentity(identityId);
        List<Machine> machines = getMachineService().getMachineRepository().findAllById(machineIds);
        identity.addMachines(machines);
        return identity.getId();
    }
}
