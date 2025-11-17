package trinity.play2learn.backend.activity.ordenarSecuencia.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "event_ordenar_secuencia_activity")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El orden es obligatorio")
    @Column(name = "event_order")
    private Integer order;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Maximum length for name is 50 characters.")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Maximum length for description is 100 characters.")
    private String description;

    private String url;

    @ManyToOne 
    @NotNull
    private OrdenarSecuencia activity; 
}
