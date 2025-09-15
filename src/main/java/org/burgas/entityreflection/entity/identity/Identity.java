package org.burgas.entityreflection.entity.identity;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.entityreflection.entity.BaseEntity;
import org.burgas.entityreflection.entity.company.Company;
import org.burgas.entityreflection.entity.machine.Machine;
import org.burgas.entityreflection.entity.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "identity", schema = "public")
@ToString(exclude = {"company", "machines", "wallets"})
@EqualsAndHashCode(callSuper = true, exclude = {"company", "machines", "wallets"})
@NamedEntityGraph(
        name = "identity-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "company"),
                @NamedAttributeNode(value = "machines"),
                @NamedAttributeNode(value = "wallets")
        }
)
public final class Identity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Embedded
    private IdentitySecure identitySecure;

    @Embedded
    private IdentityFio identityFio;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_machine",
            joinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "machine_id", referencedColumnName = "id")
    )
    private List<Machine> machines = new ArrayList<>();

    public void addMachine(final Machine machine) {
        this.machines.add(machine);
        machine.getIdentities().add(this);
    }

    public void addMachines(final List<Machine> machines) {
        this.machines.addAll(machines);
        machines.forEach(machine -> machine.getIdentities().add(this));
    }

    @Builder.Default
    @OneToMany(mappedBy = "identity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wallet> wallets = new ArrayList<>();

    public void addWallet(final Wallet wallet) {
        this.wallets.add(wallet);
        wallet.setIdentity(this);
    }

    public void addWallets(final List<Wallet> wallets) {
        this.wallets.addAll(wallets);
        wallets.forEach(wallet -> wallet.setIdentity(this));
    }
}
