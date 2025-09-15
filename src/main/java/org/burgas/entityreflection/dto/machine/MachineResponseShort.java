package org.burgas.entityreflection.dto.machine;

import lombok.*;
import org.burgas.entityreflection.dto.Response;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MachineResponseShort extends Response<UUID> {

    private String name;
    private String description;
    private Double cost;
}
