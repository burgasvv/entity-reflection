package org.burgas.entityreflection.repository;

import org.burgas.entityreflection.entity.operation.Operation;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {

    @Override
    @EntityGraph(value = "operation-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Operation> findById(@NotNull UUID uuid);
}
