package org.burgas.entityreflection.repository;

import jakarta.persistence.LockModeType;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.wallet.Wallet;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Override
    @EntityGraph(value = "wallet-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Wallet> findById(@NotNull UUID uuid);

    @Lock(value = LockModeType.PESSIMISTIC_READ)
    Optional<Wallet> findWalletById(UUID id);

    List<Wallet> findWalletsByIdentity(Identity identity);
}
