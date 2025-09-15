package org.burgas.entityreflection.dto.machine;

import lombok.*;
import org.burgas.entityreflection.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class MachineRequest extends Request<UUID> {

    private String name;
    private String description;
    private Double cost;
}
