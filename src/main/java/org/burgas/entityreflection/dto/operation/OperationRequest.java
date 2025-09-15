package org.burgas.entityreflection.dto.operation;

import lombok.*;
import org.burgas.entityreflection.dto.Request;
import org.burgas.entityreflection.entity.operation.OperationType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OperationRequest extends Request<UUID> {

    private OperationType operation;
    private Double amount;
    private UUID senderWalletId;
    private UUID receiverWalletId;
}
