package org.burgas.entityreflection.dto.wallet;

import lombok.*;
import org.burgas.entityreflection.dto.Response;
import org.burgas.entityreflection.dto.identity.IdentityResponseShort;
import org.burgas.entityreflection.dto.operation.OperationResponseShort;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletResponseFull extends Response<UUID> {

    private Double balance;
    private IdentityResponseShort identity;
    private List<OperationResponseShort> operationsBySenderWallet;
    private List<OperationResponseShort> operationsByReceiverWallet;
}
