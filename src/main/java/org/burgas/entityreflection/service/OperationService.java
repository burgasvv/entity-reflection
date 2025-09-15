package org.burgas.entityreflection.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.dto.operation.OperationRequest;
import org.burgas.entityreflection.dto.operation.OperationResponseFull;
import org.burgas.entityreflection.dto.operation.OperationResponseShort;
import org.burgas.entityreflection.entity.operation.Operation;
import org.burgas.entityreflection.exception.OperationNotFoundException;
import org.burgas.entityreflection.mapper.OperationMapper;
import org.burgas.entityreflection.message.OperationMessages;
import org.burgas.entityreflection.repository.OperationRepository;
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
public class OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final ObjectFactory<WalletService> walletServiceObjectFactory;

    private WalletService getWalletService() {
        return this.walletServiceObjectFactory.getObject();
    }

    public Operation findOperation(final UUID operationId) {
        return this.operationRepository.findById(
                        operationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : operationId
                )
                .orElseThrow(() -> new OperationNotFoundException(OperationMessages.OPERATION_NOT_FOUND.getMessage()));
    }

    public List<OperationResponseShort> findBySenderWalletId(final UUID walletId) {
        return this.getWalletService().findWallet(walletId)
                .getOperationsBySenderWallet()
                .stream()
                .map(this.operationMapper::toShortResponse)
                .collect(Collectors.toList());
    }

    public List<OperationResponseShort> findByReceiverWalletId(final UUID walletId) {
        return this.getWalletService().findWallet(walletId)
                .getOperationsByReceiverWallet()
                .stream()
                .map(this.operationMapper::toShortResponse)
                .collect(Collectors.toList());
    }

    public OperationResponseFull findById(final UUID operationId) {
        return this.operationMapper.toFullResponse(this.findOperation(operationId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(final OperationRequest operationRequest) {
        Operation entity = this.operationMapper.toEntity(operationRequest);
        return this.operationRepository.save(entity).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(final UUID operationId) {
        Operation operation = this.findOperation(operationId);
        this.operationRepository.delete(operation);
    }
}
