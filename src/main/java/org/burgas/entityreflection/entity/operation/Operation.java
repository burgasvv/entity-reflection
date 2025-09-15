package org.burgas.entityreflection.entity.operation;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.entityreflection.entity.BaseEntity;
import org.burgas.entityreflection.entity.wallet.Wallet;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "operation", schema = "public")
@NamedEntityGraph(
        name = "operation-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "senderWallet"),
                @NamedAttributeNode(value = "receiverWallet")
        }
)
public final class Operation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private OperationType operation;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_wallet_id", referencedColumnName = "id")
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_wallet_id", referencedColumnName = "id")
    private Wallet receiverWallet;
}
