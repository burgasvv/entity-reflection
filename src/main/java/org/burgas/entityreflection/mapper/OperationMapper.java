package org.burgas.entityreflection.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.operation.OperationRequest;
import org.burgas.entityreflection.dto.operation.OperationResponseFull;
import org.burgas.entityreflection.dto.operation.OperationResponseShort;
import org.burgas.entityreflection.entity.operation.Operation;
import org.burgas.entityreflection.entity.operation.OperationType;
import org.burgas.entityreflection.entity.wallet.Wallet;
import org.burgas.entityreflection.mapper.contract.EntityMapper;
import org.burgas.entityreflection.message.OperationMessages;
import org.burgas.entityreflection.repository.OperationRepository;
import org.burgas.entityreflection.service.WalletService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class OperationMapper implements EntityMapper<UUID, OperationRequest, Operation, OperationResponseFull, OperationResponseShort> {

    private final OperationRepository operationRepository;
    private final ObjectFactory<WalletService> walletServiceObjectFactory;
    private final ObjectFactory<WalletMapper> walletMapperObjectFactory;

    private WalletService getWalletService() {
        return this.walletServiceObjectFactory.getObject();
    }

    private WalletMapper getWalletMapper() {
        return this.walletMapperObjectFactory.getObject();
    }

    @Override
    public Operation toEntity(OperationRequest operationRequest) {
        UUID operationId = this.handleData(operationRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.operationRepository.findById(operationId)
                .map(
                        operation -> {
                            OperationType operationType = this.handleData(operationRequest.getOperation(), operation.getOperation());
                            Double amount = this.handleData(operationRequest.getAmount(), operation.getAmount());
                            Wallet senderWallet = this.getWalletService().findWallet(operationRequest.getSenderWalletId());
                            Wallet receiverWallet = this.getWalletService().findWallet(operationRequest.getReceiverWalletId());
                            return Operation.builder()
                                    .id(operation.getId())
                                    .operation(operationType)
                                    .amount(amount)
                                    .senderWallet(senderWallet)
                                    .receiverWallet(receiverWallet)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            OperationType operationType = this.handleDataThrowable(
                                    operationRequest.getOperation(), OperationMessages.OPERATION_TYPE_FIELD_EMPTY.getMessage()
                            );
                            Double amount = this.handleDataThrowable(
                                    operationRequest.getAmount(), OperationMessages.OPERATION_AMOUNT_FIELD_EMPTY.getMessage()
                            );
                            Wallet senderWallet = this.getWalletService().findWallet(operationRequest.getSenderWalletId());
                            Wallet receiverWallet = this.getWalletService().findWallet(operationRequest.getReceiverWalletId());
                            return Operation.builder()
                                    .operation(operationType)
                                    .amount(amount)
                                    .senderWallet(senderWallet)
                                    .receiverWallet(receiverWallet)
                                    .build();
                        }
                );
    }

    @Override
    public OperationResponseFull toFullResponse(Operation operation) {
        OperationResponseFull operationResponseFull = new OperationResponseFull();
        operationResponseFull.setId(operation.getId());
        operationResponseFull.setOperation(operation.getOperation());
        operationResponseFull.setAmount(operation.getAmount());
        operationResponseFull.setSenderWallet(
                Optional.ofNullable(operation.getSenderWallet())
                        .map(wallet -> this.getWalletMapper().toFullResponse(wallet))
                        .orElse(null)
        );
        operationResponseFull.setReceiverWallet(
                Optional.ofNullable(operation.getReceiverWallet())
                        .map(wallet -> this.getWalletMapper().toFullResponse(wallet))
                        .orElse(null)
        );
        return operationResponseFull;
    }

    @Override
    public OperationResponseShort toShortResponse(Operation operation) {
        OperationResponseShort operationResponseShort = new OperationResponseShort();
        operationResponseShort.setId(operation.getId());
        operationResponseShort.setOperation(operation.getOperation());
        operationResponseShort.setAmount(operation.getAmount());
        return operationResponseShort;
    }
}
