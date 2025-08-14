package trinity.play2learn.backend.economy.reserve.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reserve")
public class Reserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double reserveBalance;

    private Double circulationBalance;

    private Double initialBalance;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime lastUpdateAt;

    @OneToMany(mappedBy = "reserve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaccion> transaccions = new ArrayList<>();

    @PrePersist //Antes de persistir la actividad se guarda su fecha de creacion
    private void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }

    private void modifiedAt(){
        this.lastUpdateAt = LocalDateTime.now();
    }
    
}
