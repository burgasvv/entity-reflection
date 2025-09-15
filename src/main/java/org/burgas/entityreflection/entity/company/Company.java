package org.burgas.entityreflection.entity.company;

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
@ToString(exclude = {"identities"})
@EqualsAndHashCode(callSuper = true, exclude = {"identities"})
@Table(name = "company", schema = "public")
@NamedEntityGraph(
        name = "company-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "identities")
        }
)
public final class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Builder.Default
    @OneToMany(
            mappedBy = "company",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private List<Identity> identities = new ArrayList<>();

    public void addIdentity(final Identity identity) {
        this.identities.add(identity);
        identity.setCompany(this);
    }

    public void addIdentities(final List<Identity> identities) {
        this.identities.addAll(identities);
        identities.forEach(identity -> identity.setCompany(this));
    }
}
