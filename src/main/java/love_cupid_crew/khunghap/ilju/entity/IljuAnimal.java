package love_cupid_crew.khunghap.ilju.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ilju_animal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class IljuAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false, length = 10)
    private String emoji;

    @Column(nullable = false, length = 5)
    private String cheongan;

    @Column(nullable = false, length = 5)
    private String jiji;

    @Column(nullable = false, length = 5)
    private String oheng;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT[]")
    private String[] personality;

    @Column(columnDefinition = "TEXT[]")
    private String[] strengths;

    @Column(columnDefinition = "TEXT[]")
    private String[] weaknesses;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;
}