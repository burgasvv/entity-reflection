package org.burgas.entityreflection.dto.identity;

import lombok.*;
import org.burgas.entityreflection.dto.Response;
import org.burgas.entityreflection.dto.company.CompanyResponseShort;
import org.burgas.entityreflection.dto.machine.MachineResponseShort;
import org.burgas.entityreflection.entity.identity.IdentityFio;
import org.burgas.entityreflection.entity.identity.IdentitySecure;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityResponseFull extends Response<UUID> {

    private IdentitySecure identitySecure;
    private IdentityFio identityFio;
    private CompanyResponseShort company;
    private List<MachineResponseShort> machines;
}
