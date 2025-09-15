package org.burgas.entityreflection.repository;

import org.burgas.entityreflection.entity.machine.Machine;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID> {

    @Override
    @EntityGraph(value = "machine-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Machine> findById(@NotNull UUID uuid);
}
