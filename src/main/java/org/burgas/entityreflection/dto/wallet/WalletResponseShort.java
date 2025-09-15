package org.burgas.entityreflection.dto.wallet;

import lombok.*;
import org.burgas.entityreflection.dto.Response;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class WalletResponseShort extends Response<UUID> {

    private Double balance;
}
