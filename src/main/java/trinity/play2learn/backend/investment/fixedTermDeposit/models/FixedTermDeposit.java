package trinity.play2learn.backend.investment.fixedTermDeposit.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixed_term_deposit")
public class FixedTermDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amountInvested;

    private Double amountReward;

    private FixedTermDays fixedTermDays;

    private LocalDate startDate;

    private LocalDate endDate;

    private FixedTermState fixedTermState;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    
}
