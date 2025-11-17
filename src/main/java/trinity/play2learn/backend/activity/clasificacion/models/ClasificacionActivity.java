package trinity.play2learn.backend.activity.clasificacion.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@SuperBuilder
@Table(name = "clasificacion_activity")
public class ClasificacionActivity extends Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.PERSIST} , mappedBy = "activity")
    @Size(min = 2, max = 10,  message = "The activity must have between 2 and 10 categories.")
    private List<CategoryClasificacion> categories;

    public void setCategories(List<CategoryClasificacion> categories) {
        categories.forEach(category -> category.setActivity(this));
        this.categories = categories;
    }

}
