package org.burgas.entityreflection.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.machine.MachineRequest;
import org.burgas.entityreflection.dto.machine.MachineResponseFull;
import org.burgas.entityreflection.dto.machine.MachineResponseShort;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.machine.Machine;
import org.burgas.entityreflection.exception.MachineNotFoundException;
import org.burgas.entityreflection.mapper.MachineMapper;
import org.burgas.entityreflection.message.MachineMessages;
import org.burgas.entityreflection.repository.MachineRepository;
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
public class MachineService implements CrudService<UUID, MachineRequest, MachineResponseFull, MachineResponseShort> {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;
    private final ObjectFactory<IdentityService> identityServiceObjectFactory;

    private IdentityService getIdentityService() {
        return this.identityServiceObjectFactory.getObject();
    }

    public Machine findMachine(final UUID machineId) {
        return this.machineRepository.findById(
                        machineId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : machineId
                )
                .orElseThrow(
                        () -> new MachineNotFoundException(MachineMessages.MACHINE_NOT_FOUND.getMessage())
                );
    }

    @Override
    public List<MachineResponseShort> findAll() {
        return this.machineRepository.findAll()
                .stream()
                .map(this.machineMapper::toShortResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MachineResponseFull findById(UUID uuid) {
        return this.machineMapper.toFullResponse(this.findMachine(uuid));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(MachineRequest machineRequest) {
        return this.machineRepository.save(this.machineMapper.toEntity(machineRequest)).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(UUID uuid) {
        Machine machine = this.findMachine(uuid);
        this.machineRepository.delete(machine);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID addIdentity(final UUID machineId, final UUID identityId) {
        Machine machine = this.findMachine(machineId);
        Identity identity = getIdentityService().findIdentity(identityId);
        machine.addIdentity(identity);
        return machine.getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID addIdentities(final UUID machineId, final List<UUID> identityIds) {
        Machine machine = this.findMachine(machineId);
        List<Identity> identities = getIdentityService().getIdentityRepository().findAllById(identityIds);
        machine.addIdentities(identities);
        return machine.getId();
    }
}
