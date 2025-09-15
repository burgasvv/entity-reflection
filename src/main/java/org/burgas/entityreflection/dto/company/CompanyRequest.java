package org.burgas.entityreflection.dto.company;

import lombok.*;
import org.burgas.entityreflection.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CompanyRequest extends Request<UUID> {

    private String name;
    private String description;
}
