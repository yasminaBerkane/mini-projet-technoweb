package pharmacie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

@Service
public class ApprovisionnementService {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public void lancerApprovisionnement() {
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

        try {
            StringBuilder contenu = new StringBuilder();

            contenu.append("Bonjour ").append(fournisseur.getNom()).append(",\n\n");
            contenu.append("Merci de nous transmettre un devis pour les médicaments suivants :\n\n");

            data.forEach((categorie, meds) -> {
                contenu.append("Catégorie : ")
                        .append(categorie.getLibelle())
                        .append("\n");

                meds.forEach(m ->
                        contenu.append(" - ").append(m.getNom())
                                .append(" (stock: ")
                                .append(m.getUnitesEnStock())
                                .append(", seuil: ")
                                .append(m.getNiveauDeReappro())
                                .append(")\n")
                );

                contenu.append("\n");
            });

            Email from = new Email(fromEmail);
            Email to = new Email(fournisseur.getEmail());
            Content content = new Content("text/plain", contenu.toString());

            Mail mail = new Mail(from,
                    "Demande de devis – Réapprovisionnement pharmacie",
                    to,
                    content);

            SendGrid sg = new SendGrid(sendGridApiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                System.err.println("Erreur SendGrid : " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}