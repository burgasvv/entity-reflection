package org.burgas.entityreflection.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.wallet.WalletRequest;
import org.burgas.entityreflection.dto.wallet.WalletResponseFull;
import org.burgas.entityreflection.dto.wallet.WalletResponseShort;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.wallet.Wallet;
import org.burgas.entityreflection.mapper.contract.EntityMapper;
import org.burgas.entityreflection.repository.WalletRepository;
import org.burgas.entityreflection.service.IdentityService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class WalletMapper implements EntityMapper<UUID, WalletRequest, Wallet, WalletResponseFull, WalletResponseShort> {

    private final WalletRepository walletRepository;
    private final ObjectFactory<IdentityService> identityServiceObjectFactory;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<OperationMapper> operationMapperObjectFactory;

    private IdentityService getIdentityService() {
        return this.identityServiceObjectFactory.getObject();
    }

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private OperationMapper getOperationMapper() {
        return this.operationMapperObjectFactory.getObject();
    }

    @Override
    public Wallet toEntity(WalletRequest walletRequest) {
        UUID walletId = this.handleData(walletRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.walletRepository.findById(walletId)
                .map(
                        wallet -> {
                            Identity identity = this.getIdentityService().findIdentity(walletRequest.getIdentityId());
                            return Wallet.builder()
                                    .id(wallet.getId())
                                    .balance(wallet.getBalance())
                                    .identity(identity)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            Identity identity = this.getIdentityService().findIdentity(walletRequest.getIdentityId());
                            return Wallet.builder()
                                    .balance(0.0)
                                    .identity(identity)
                                    .build();
                        }
                );
    }

    @Override
    public WalletResponseFull toFullResponse(Wallet wallet) {
        WalletResponseFull walletResponseFull = new WalletResponseFull();
        walletResponseFull.setId(wallet.getId());
        walletResponseFull.setBalance(wallet.getBalance());
        walletResponseFull.setIdentity(
                Optional.ofNullable(wallet.getIdentity())
                        .map(identity -> this.getIdentityMapper().toShortResponse(identity))
                        .orElse(null)
        );
        walletResponseFull.setOperationsBySenderWallet(
                wallet.getOperationsBySenderWallet() == null ? null : wallet.getOperationsBySenderWallet()
                        .stream()
                        .map(operation -> this.getOperationMapper().toShortResponse(operation))
                        .toList()
        );
        walletResponseFull.setOperationsByReceiverWallet(
                wallet.getOperationsByReceiverWallet() == null ? null : wallet.getOperationsByReceiverWallet()
                        .stream()
                        .map(operation -> this.getOperationMapper().toShortResponse(operation))
                        .toList()
        );
        return walletResponseFull;
    }

    @Override
    public WalletResponseShort toShortResponse(Wallet wallet) {
        WalletResponseShort walletResponseShort = new WalletResponseShort();
        walletResponseShort.setId(wallet.getId());
        walletResponseShort.setBalance(wallet.getBalance());
        return walletResponseShort;
    }
}
