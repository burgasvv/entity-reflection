package org.burgas.entityreflection.entity.machine;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.entityreflection.entity.BaseEntity;
import org.burgas.entityreflection.entity.identity.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "identities")
@ToString(exclude = "identities")
@Table(name = "machine", schema = "public")
@NamedEntityGraph(
        name = "machine-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "identities")
        }
)
public final class Machine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Builder.Default
    @ManyToMany(mappedBy = "machines", fetch = FetchType.LAZY)
    private List<Identity> identities = new ArrayList<>();

    public void addIdentity(final Identity identity) {
        this.identities.add(identity);
        identity.getMachines().add(this);
    }

    public void addIdentities(final List<Identity> identities) {
        this.identities.addAll(identities);
        identities.forEach(identity -> identity.getMachines().add(this));
    }
}
