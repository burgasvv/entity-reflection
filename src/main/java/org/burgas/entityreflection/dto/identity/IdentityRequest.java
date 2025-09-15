package org.burgas.entityreflection.dto.identity;

import lombok.*;
import org.burgas.entityreflection.dto.Request;
import org.burgas.entityreflection.entity.identity.IdentityFio;
import org.burgas.entityreflection.entity.identity.IdentitySecure;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityRequest extends Request<UUID> {

    private IdentitySecure identitySecure;
    private IdentityFio identityFio;
    private UUID companyId;
}
