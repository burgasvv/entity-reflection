package org.burgas.entityreflection.dto.operation;

import lombok.*;
import org.burgas.entityreflection.dto.Response;
import org.burgas.entityreflection.entity.operation.OperationType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OperationResponseShort extends Response<UUID> {

    private OperationType operation;
    private Double amount;
}
