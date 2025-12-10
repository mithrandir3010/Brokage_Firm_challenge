package broker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
    name = "assets",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_asset_customer_name",
        columnNames = {"customer_id", "asset_name"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal size;

    @Column(name = "usable_size", nullable = false, precision = 19, scale = 8)
    private BigDecimal usableSize;

}
