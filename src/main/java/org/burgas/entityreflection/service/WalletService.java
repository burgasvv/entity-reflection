package org.burgas.entityreflection.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.operation.OperationRequest;
import org.burgas.entityreflection.dto.wallet.WalletRequest;
import org.burgas.entityreflection.dto.wallet.WalletResponseFull;
import org.burgas.entityreflection.dto.wallet.WalletResponseShort;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.operation.OperationType;
import org.burgas.entityreflection.entity.wallet.Wallet;
import org.burgas.entityreflection.exception.NotEnoughWalletBalanceException;
import org.burgas.entityreflection.exception.SameWalletException;
import org.burgas.entityreflection.exception.WalletNotFoundException;
import org.burgas.entityreflection.mapper.WalletMapper;
import org.burgas.entityreflection.message.WalletMessages;
import org.burgas.entityreflection.repository.WalletRepository;
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
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final ObjectFactory<IdentityService> identityServiceObjectFactory;
    private final ObjectFactory<OperationService> operationServiceObjectFactory;

    private IdentityService getidentityService() {
        return this.identityServiceObjectFactory.getObject();
    }

    private OperationService getOperationService() {
        return this.operationServiceObjectFactory.getObject();
    }

    public Wallet findWallet(final UUID walletId) {
        return this.walletRepository.findById(
                        walletId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : walletId
                )
                .orElseThrow(
                        () -> new WalletNotFoundException(WalletMessages.WALLET_NOT_FOUND.getMessage())
                );
    }

    private Wallet findWalletPessimisticRead(final UUID walletId) {
        return this.walletRepository.findWalletById(
                        walletId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : walletId
                )
                .orElseThrow(
                        () -> new WalletNotFoundException(WalletMessages.WALLET_NOT_FOUND.getMessage())
                );
    }

    public List<WalletResponseShort> findWalletsByIdentity(final UUID identityId) {
        Identity identity = this.getidentityService().findIdentity(identityId);
        return this.walletRepository.findWalletsByIdentity(identity)
                .stream()
                .map(this.walletMapper::toShortResponse)
                .collect(Collectors.toList());
    }

    public WalletResponseFull findById(final UUID walletId) {
        return this.walletMapper.toFullResponse(this.findWallet(walletId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(final WalletRequest walletRequest) {
        Wallet entity = this.walletMapper.toEntity(walletRequest);
        return this.walletRepository.save(entity).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(final UUID walletId) {
        Wallet wallet = this.findWallet(walletId);
        this.walletRepository.delete(wallet);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID deposit(final UUID walletId, final Double amount) {
        Wallet wallet = this.findWalletPessimisticRead(walletId);
        wallet.setBalance(wallet.getBalance() + amount);
        OperationRequest operationRequest = OperationRequest.builder()
                .amount(amount)
                .operation(OperationType.DEPOSIT)
                .senderWalletId(wallet.getId())
                .receiverWalletId(wallet.getId())
                .build();
        return this.getOperationService().createOrUpdate(operationRequest);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID withdraw(final UUID walletId, final Double amount) {
        Wallet wallet = this.findWalletPessimisticRead(walletId);
        if (wallet.getBalance() < amount)
            throw new NotEnoughWalletBalanceException(WalletMessages.NOT_ENOUGH_WALLET_BALANCE.getMessage());
        wallet.setBalance(wallet.getBalance() - amount);
        OperationRequest operationRequest = OperationRequest.builder()
                .amount(amount)
                .operation(OperationType.WITHDRAW)
                .senderWalletId(wallet.getId())
                .receiverWalletId(wallet.getId())
                .build();
        return this.getOperationService().createOrUpdate(operationRequest);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID transfer(final UUID senderId, final UUID receiverId, final Double amount) {
        if (senderId.equals(receiverId))
            throw new SameWalletException(WalletMessages.SAME_SENDER_AND_RECEIVER_WALLET.getMessage());

        Wallet sender = this.findWalletPessimisticRead(senderId);
        Wallet receiver = this.findWalletPessimisticRead(receiverId);

        if (sender.getBalance() < amount)
            throw new NotEnoughWalletBalanceException(WalletMessages.NOT_ENOUGH_WALLET_BALANCE.getMessage());

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        OperationRequest operationRequest = OperationRequest.builder()
                .amount(amount)
                .operation(OperationType.TRANSFER)
                .senderWalletId(sender.getId())
                .receiverWalletId(receiver.getId())
                .build();

        return this.getOperationService().createOrUpdate(operationRequest);
    }
}
