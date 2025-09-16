package org.burgas.entityreflection.entity.wallet;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.entityreflection.entity.BaseEntity;
import org.burgas.entityreflection.entity.identity.Identity;
import org.burgas.entityreflection.entity.operation.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "wallet", schema = "public")
@NamedEntityGraph(
        name = "wallet-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "identity"),
                @NamedAttributeNode(value = "operationsBySenderWallet"),
                @NamedAttributeNode(value = "operationsByReceiverWallet")
        }
)
public final class Wallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id", referencedColumnName = "id")
    private Identity identity;

    @Builder.Default
    @OneToMany(mappedBy = "senderWallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Operation> operationsBySenderWallet = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiverWallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Operation> operationsByReceiverWallet = new ArrayList<>();
}
