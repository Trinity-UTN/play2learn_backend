package trinity.play2learn.backend.activity.preguntados.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "questions")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @Valid //Sirve para validar restricciones dentro de los elementos
    @Size(min = 4, max = 4, message = "Must have 4 options")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , mappedBy = "question")
    private List<Option> options;

    @ManyToOne
    @JoinColumn(name = "preguntados_id")
    private Preguntados preguntados;

    public void setOptions(List<Option> options) {
        if (options != null) {
            options.forEach(option -> option.setQuestion(this)); //Relaciono cada opcion con la pregunta
        }
        this.options = options;
    }
}
