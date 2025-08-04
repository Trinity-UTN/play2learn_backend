package trinity.play2learn.backend.activity.arbolDeDecision.models;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DecisionArbolDecision {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String name;

    @ManyToOne
    @NotNull
    private ArbolDeDecisionActivity activity;

    @OneToMany(mappedBy = "previousDecision", cascade = {CascadeType.PERSIST , CascadeType.MERGE}) //Relacion 0 o 1
    @Column(nullable = true)
    private List<DecisionArbolDecision> options;

    @ManyToOne(optional = true)
    private DecisionArbolDecision previousDecision;

    @OneToOne(optional = true, cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    private ConsecuenceArbolDecision consecuence;

    public void setOptions(List<DecisionArbolDecision> options) {
        if (options != null){

            options.forEach(option -> option.setPreviousDecision(this));
        }
        this.options = options;
    }

    public void setActivity(ArbolDeDecisionActivity activity) {
        
        this.activity = activity;
        options.forEach(option -> option.setActivity(activity));
    }

}
