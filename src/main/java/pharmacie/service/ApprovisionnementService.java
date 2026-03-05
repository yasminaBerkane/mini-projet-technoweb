package pharmacie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

@Service
public class ApprovisionnementService {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Lance le processus de réapprovisionnement :
     * - cherche les médicaments sous le seuil
     * - regroupe par fournisseur et par catégorie
     * - envoie un mail par fournisseur
     */
    public void lancerApprovisionnement() {
        System.out.println("Méthode appelée !");
        List<Medicament> aCommander = medicamentRepository.medicamentsAReapprovisionner();

        Map<Fournisseur, Map<Categorie, List<Medicament>>> regroupement = new HashMap<>();

        for (Medicament m : aCommander) {
            Categorie categorie = m.getCategorie();

            for (Fournisseur fournisseur : categorie.getFournisseurs()) {
                regroupement
                    .computeIfAbsent(fournisseur, f -> new HashMap<>())
                    .computeIfAbsent(categorie, c -> new ArrayList<>())
                    .add(m);
            }
        }

        regroupement.forEach(this::envoyerMail);
    }

    private void envoyerMail(Fournisseur fournisseur,
                             Map<Categorie, List<Medicament>> data) {

        StringBuilder contenu = new StringBuilder();
        contenu.append("Bonjour ").append(fournisseur.getNom()).append(",\n\n");
        contenu.append("Merci de nous transmettre un devis pour les médicaments suivants :\n\n");

        data.forEach((categorie, meds) -> {
            contenu.append("Catégorie : ").append(categorie.getLibelle()).append("\n");
            meds.forEach(m ->
                contenu.append(" - ").append(m.getNom())
                       .append(" (stock: ").append(m.getUnitesEnStock())
                       .append(", seuil: ").append(m.getNiveauDeReappro())
                       .append(")\n")
            );
            contenu.append("\n");
        });

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yasminabke2005@gmail.com");
        message.setTo(fournisseur.getEmail());
        message.setSubject("Demande de devis – Réapprovisionnement pharmacie");
        message.setText(contenu.toString());

        mailSender.send(message);
    }
}
