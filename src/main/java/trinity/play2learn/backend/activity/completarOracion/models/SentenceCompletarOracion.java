package trinity.play2learn.backend.activity.completarOracion.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentenceCompletarOracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String completeSentence;

    @ManyToOne
    private CompletarOracionActivity activity;

    @OneToMany(mappedBy = "sentence", cascade = CascadeType.PERSIST)
    private List<WordCompletarOracion> words;

    private LocalDateTime deletedAt;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

    public void setWords(List<WordCompletarOracion> words) {
        words.forEach(word -> word.setSentence(this));
        this.words = words;
    }

    //Ordena la lista de palabras segun el atributo order
    public void orderWords() {

        List<WordCompletarOracion> words = new ArrayList<>(getWords()); // Convierto a una lista modificable

        words.sort(Comparator.comparingInt(WordCompletarOracion::getWordOrder)); // ordenar
        
        setWords(words); // guardar la lista ordenada
    }


    //Arma la oracion completa agregando espacios en base a las palabras
    public void buildCompleteSentence(){

        orderWords(); //Ordeno la lista de palabras antes de armar la oracion

        List<String> punctuationMarksWithNoSpaceBefore = List.of(".", ",", ";", ":", "!", "?", ")", "...", "$"); //No llevan espacios despues
        List<String> punctuationMarksWithNoSpaceAfter = List.of("¿", "¡", "(", "*", "%"); //No llevan espacios antes

        StringBuilder sb = new StringBuilder(); //De no utilizar esto, se estaria creando un nuevo string en cada iteracion
        sb.append(this.getWords().get(0).getWord());// Inicializo la oracion con la primera palabra

        for (int i = 1; i < this.getWords().size(); i++) {
            String currentWord = this.getWords().get(i).getWord();
            String previousWord = this.getWords().get(i - 1).getWord();

            // Si la palabra actual no es un signo de puntuacion sin espacios antes y la palabra anterior no es un signo de puntuacion sin espacios despues
            if (!punctuationMarksWithNoSpaceBefore.contains(currentWord) && !punctuationMarksWithNoSpaceAfter.contains(previousWord)) {
                sb.append(" ");
            }
            sb.append(currentWord);
        }

        if (sb.length() > 0) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0))); //Primer letra en mayuscula
        }

        this.setCompleteSentence(sb.toString());
    }

}
