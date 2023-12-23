package ma.emsi.voitureService.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marque;
    private String matricule;
    private String model;
    private Long id_client;
    @Transient
    @ManyToOne
    private Client client;
}