package trinity.play2learn.backend.investment.stock.models;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column (length = 50)
    private String name;

    private String abbreviation;

    private BigInteger totalAmount;

    private BigInteger availableAmount;

    private BigInteger soldAmount;

    private Double currentPrice;

    private Double initialPrice;
    
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
}
