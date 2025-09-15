package org.burgas.entityreflection.dto.company;

import lombok.*;
import org.burgas.entityreflection.dto.Response;
import org.burgas.entityreflection.dto.identity.IdentityResponseShort;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CompanyResponseFull extends Response<UUID> {

    private String name;
    private String description;
    private List<IdentityResponseShort> identities;
}
