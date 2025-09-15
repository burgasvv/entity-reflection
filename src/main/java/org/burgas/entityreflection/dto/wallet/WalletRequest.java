package org.burgas.entityreflection.dto.wallet;

import lombok.*;
import org.burgas.entityreflection.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class WalletRequest extends Request<UUID> {

    private UUID identityId;
}
