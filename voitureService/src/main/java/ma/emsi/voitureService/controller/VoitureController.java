package ma.emsi.voitureService.controller;

import ma.emsi.voitureService.entities.Client;
import ma.emsi.voitureService.entities.Voiture;
import ma.emsi.voitureService.repository.VoitureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class VoitureController {

    private final VoitureRepository voitureRepository;

    private final RestTemplate restTemplate;

    public VoitureController(VoitureRepository voitureRepository, RestTemplate restTemplate) {
        this.voitureRepository = voitureRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/voitures", produces = {"application/json"})
    public ResponseEntity<Object> findAll() {
        try {
            List<Voiture> voiture = voitureRepository.findAll();
            List<Voiture> voitures = voiture.stream()
                    .map(v -> {
                        v.setClient(fetchClientById(v.getId_client()));
                        return v;
                    }).toList();
            return ResponseEntity.ok(voitures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching voitures: " + e.getMessage());
        }
    }

    @GetMapping("/voitures/{Id}")
    public ResponseEntity<Object> findById(@PathVariable Long Id) {
        try {
            Voiture voiture = voitureRepository.findById(Id)
                    .orElseThrow(() -> new Exception("Voiture Introuvable"));
            voiture.setClient(fetchClientById(voiture.getId_client()));

            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Voiture not found with ID: " + Id);
        }
    }

    @GetMapping("/voitures/client/{Id}")
    public ResponseEntity<List<Voiture>> findByClient(@PathVariable Long Id) {
        try {
            Client client = fetchClientById(Id);
            if (client != null) {
                List<Voiture> voitures = voitureRepository.findVoituresByClientId(Id);
                return ResponseEntity.ok(voitures);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/voitures/{clientId}")
    public ResponseEntity<Object> save(@PathVariable Long clientId, @RequestBody Voiture voiture) {
        try {
            Client client = fetchClientById(clientId);

            if (client != null) {
                voiture.setId_client(clientId);
                voiture.setClient(new Client(client.getId(),client.getNom(), client.getAge()));
                return ResponseEntity.ok(voitureRepository.save(voiture));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Client not found with ID: " + clientId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving voiture: " + e.getMessage());
        }
    }

    @PutMapping("/voitures/{Id}")
    public ResponseEntity<Object> update(@PathVariable Long Id, @RequestBody Voiture updatedVoiture) {
        try {
            Voiture existingVoiture = voitureRepository.findById(Id)
                    .orElseThrow(() -> new Exception("Voiture not found with ID: " + Id));

            // Update only the non-null fields from the request body
            if (updatedVoiture.getMatricule() != null && !updatedVoiture.getMatricule().isEmpty()) {
                existingVoiture.setMatricule(updatedVoiture.getMatricule());
            }

            if (updatedVoiture.getMarque() != null && !updatedVoiture.getMarque().isEmpty()) {
                existingVoiture.setMarque(updatedVoiture.getMarque());
            }

            if (updatedVoiture.getModel() != null && !updatedVoiture.getModel().isEmpty()) {
                existingVoiture.setModel(updatedVoiture.getModel());
            }

            Voiture savedVoiture = voitureRepository.save(existingVoiture);

            return ResponseEntity.ok(savedVoiture);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating voiture: " + e.getMessage());
        }
    }

    private Client fetchClientById(Long clientId) {
        String clientServiceUrl = "http://SERVICE-CLIENT/clients/" + clientId;
        return restTemplate.getForObject(clientServiceUrl, Client.class);
    }
}