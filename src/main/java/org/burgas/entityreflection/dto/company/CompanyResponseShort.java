package org.burgas.entityreflection.dto.company;

import lombok.*;
import org.burgas.entityreflection.dto.Response;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CompanyResponseShort extends Response<UUID> {

    private String name;
    private String description;
}
