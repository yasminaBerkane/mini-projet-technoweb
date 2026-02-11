package pharmacie.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;

@RestController
@RequestMapping("/api/approvisionnement")
public class ApprovisionnementController {

    @Autowired
    private ApprovisionnementService service;

    /**
     * Déclenche le service métier d'approvisionnement.
     */
    @PostMapping("/lancer")
    public ResponseEntity<String> lancerApprovisionnement() {
        service.lancerApprovisionnement();
        return ResponseEntity.ok("Approvisionnement lancé : mails envoyés aux fournisseurs 📧");
    }
}
