package org.burgas.entityreflection.dto.identity;

import lombok.*;
import org.burgas.entityreflection.dto.Response;
import org.burgas.entityreflection.entity.identity.IdentityFio;
import org.burgas.entityreflection.entity.identity.IdentitySecure;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityResponseShort extends Response<UUID> {

    private IdentitySecure identitySecure;
    private IdentityFio identityFio;
}
