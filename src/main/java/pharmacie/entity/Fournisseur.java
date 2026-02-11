package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @NotBlank
    private String nom;

    @NonNull
    @Email
    @NotBlank
    private String email;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
        name = "fournisseur_categorie",
        joinColumns = @JoinColumn(name = "fournisseur_id"),
        inverseJoinColumns = @JoinColumn(name = "categorie_code")
    )
    @JsonIgnoreProperties("fournisseurs")
    private List<Categorie> categories = new LinkedList<>();
}
