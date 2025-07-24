package trinity.play2learn.backend.activity.preguntados.models;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.Activity;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@Table(name = "preguntados") 
public class Preguntados extends Activity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(10) //Minimo 10 segundos
    private int maxTimePerQuestion; //segundos

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "preguntados") //Al persistir una actividad se persisten sus preguntas
    private List<Question> questions; //Preguntas con sus posibles respuestas

    public void setQuestions(List<Question> questions) {
        if (questions != null) {
            questions.forEach(question -> question.setPreguntados(this)); //Relaciono cada pregunta con la actividad Preguntados
        }
        this.questions = questions;
    }
}
