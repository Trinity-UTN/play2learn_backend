package trinity.play2learn.backend.activity.arbolDeDecision.models;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.Activity;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class ArbolDeDecisionActivity extends Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //En base a esta introduccion, se tendra que elegir entre las dos primeras opciones(Sirve tambien como una introduccion a la situacion)
    @NotBlank
    @Size(max = 500)
    private String introduction;

    @OneToMany(mappedBy = "activity" , cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @NotNull
    private List<DecisionArbolDecision> decisionTree;

    public void setDecisionTree(List<DecisionArbolDecision> decisionTree) {
        decisionTree.forEach(decision -> decision.setActivity(this));
        this.decisionTree = decisionTree;
    }
}
