package ma.emsi.voitureService.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private Long id;
    private String nom;
    private Float age;
}
