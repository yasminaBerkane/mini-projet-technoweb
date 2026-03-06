package pharmacie.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ReapprovisionnementService;

@RestController
@RequestMapping("/api/reapprovisionnement")
public class ReapprovisionnementController {

    @Autowired
    private ReapprovisionnementService service;

    @PostMapping("/lancer")
    public ResponseEntity<String> lancerReapprovisionnement() {
        service.lancerReapprovisionnement();
        return ResponseEntity.ok("Reapprovisionnement lancé : mails envoyés aux fournisseurs 📧");
    }

    @GetMapping("/test")
    public String test() {
        return "Backend OK";
    }
}
