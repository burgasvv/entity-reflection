package org.burgas.entityreflection.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.identity.IdentityResponseShort;
import org.burgas.entityreflection.dto.machine.MachineRequest;
import org.burgas.entityreflection.dto.machine.MachineResponseFull;
import org.burgas.entityreflection.dto.machine.MachineResponseShort;
import org.burgas.entityreflection.entity.machine.Machine;
import org.burgas.entityreflection.mapper.contract.EntityMapper;
import org.burgas.entityreflection.repository.MachineRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.burgas.entityreflection.message.MachineMessages.*;

@Component
@RequiredArgsConstructor
public final class MachineMapper implements EntityMapper<UUID, MachineRequest, Machine, MachineResponseFull, MachineResponseShort> {

    private final MachineRepository machineRepository;

    @Override
    public Machine toEntity(MachineRequest machineRequest) {
        UUID machineId = this.handleData(machineRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.machineRepository.findById(machineId)
                .map(
                        machine -> {
                            String name = this.handleData(machineRequest.getName(), machine.getName());
                            String description = this.handleData(machineRequest.getDescription(), machine.getDescription());
                            Double cost = this.handleData(machineRequest.getCost(), machine.getCost());

                            return Machine.builder()
                                    .id(machine.getId())
                                    .name(name)
                                    .description(description)
                                    .cost(cost)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String name = this.handleDataThrowable(machineRequest.getName(), MACHINE_NAME_FIELD_EMPTY.getMessage());
                            String description = this.handleDataThrowable(
                                    machineRequest.getDescription(), MACHINE_DESCRIPTION_FIELD_EMPTY.getMessage()
                            );
                            Double cost = this.handleDataThrowable(machineRequest.getCost(), MACHINE_FIELD_COST_EMPTY.getMessage());

                            return Machine.builder()
                                    .name(name)
                                    .description(description)
                                    .cost(cost)
                                    .build();
                        }
                );
    }

    @Override
    public MachineResponseFull toFullResponse(Machine machine) {
        MachineResponseFull machineResponseFull = new MachineResponseFull();
        machineResponseFull.setId(machine.getId());
        machineResponseFull.setName(machine.getName());
        machineResponseFull.setDescription(machine.getDescription());
        machineResponseFull.setCost(machine.getCost());
        machineResponseFull.setIdentities(
                machine.getIdentities() == null ? null : machine.getIdentities()
                        .stream()
                        .map(
                                identity -> {
                                    IdentityResponseShort identityResponseShort = new IdentityResponseShort();
                                    identityResponseShort.setId(identity.getId());
                                    identityResponseShort.setIdentitySecure(identity.getIdentitySecure());
                                    identityResponseShort.setIdentityFio(identity.getIdentityFio());
                                    return identityResponseShort;
                                }
                        )
                        .toList()
        );
        return machineResponseFull;
    }

    @Override
    public MachineResponseShort toShortResponse(Machine machine) {
        MachineResponseShort machineResponseShort = new MachineResponseShort();
        machineResponseShort.setId(machine.getId());
        machineResponseShort.setName(machine.getName());
        machineResponseShort.setDescription(machine.getDescription());
        machineResponseShort.setCost(machine.getCost());
        return machineResponseShort;
    }
}
