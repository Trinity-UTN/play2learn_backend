package trinity.play2learn.backend.activity.clasificacion.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "category_clasificacion")
public class CategoryClasificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50, message = "Maximum length for name is 50 characters.")
    @NotBlank(message = "Name is required")
    private String name;

    @OneToMany(mappedBy = "category" , cascade = CascadeType.PERSIST)
    @Size(min = 1, max = 10,  message = "The category must have between 1 and 10 concepts.")
    private List<ConceptClasificacion> concepts;

    @ManyToOne
    @NotNull
    private ClasificacionActivity activity;

    public void setConcepts(List<ConceptClasificacion> concepts) {
        concepts.forEach(concept -> concept.setCategory(this));
        this.concepts = concepts;
    }

}
