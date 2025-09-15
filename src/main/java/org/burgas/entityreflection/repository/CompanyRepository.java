package org.burgas.entityreflection.repository;

import org.burgas.entityreflection.entity.company.Company;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Override
    @EntityGraph(value = "company-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Company> findById(@NotNull UUID uuid);
}
