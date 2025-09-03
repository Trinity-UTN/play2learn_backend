package trinity.play2learn.backend.activity.completarOracion.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class CompletarOracionActivity extends Activity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "activity" , cascade = CascadeType.PERSIST)
    private List<SentenceCompletarOracion> sentences;
    
    public void setSentences(List<SentenceCompletarOracion> sentences) {
        sentences.forEach(sentence -> sentence.setActivity(this));
        this.sentences = sentences;
    }

    //Arma la oracion completa de cada oracion de la actividad
    public void buildCompleteSentences(){
        sentences.forEach(SentenceCompletarOracion::buildCompleteSentence);
    }
}
