package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import love_cupid_crew.khunghap.user.enums.BirthHour;
import love_cupid_crew.khunghap.user.enums.Gender;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private BirthHour birthHour;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 100)
    private String college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ilju_animal_id", nullable = false)
    private IljuAnimal iljuAnimal;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // ===== 역참조(Inverse) 관계 =====
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPhoto> userPhotos;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHobby> userHobbies;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPreference> userPreferences;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserLifestyle userLifestyle;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserDatingStyle userDatingStyle;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CoinBalance coinBalance;

    // ===== 편의 메서드: 업데이트 및 컬렉션 관리 =====
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setUserLifestyle(UserLifestyle lifestyle) {
        this.userLifestyle = lifestyle;
        if (lifestyle != null) {
            lifestyle.setUser(this);
        }
    }

    public void setUserDatingStyle(UserDatingStyle datingStyle) {
        this.userDatingStyle = datingStyle;
        if (datingStyle != null) {
            datingStyle.setUser(this);
        }
    }

    public void replaceHobbies(java.util.List<UserHobby> hobbies) {
        if (this.userHobbies == null) {
            this.userHobbies = new java.util.ArrayList<>();
        } else {
            this.userHobbies.clear();
        }
        if (hobbies != null) {
            for (UserHobby h : hobbies) {
                h.setUser(this);
                this.userHobbies.add(h);
            }
        }
    }

    public void replacePreferences(java.util.List<UserPreference> preferences) {
        if (this.userPreferences == null) {
            this.userPreferences = new java.util.ArrayList<>();
        } else {
            this.userPreferences.clear();
        }
        if (preferences != null) {
            for (UserPreference p : preferences) {
                p.setUser(this);
                this.userPreferences.add(p);
            }
        }
    }

    public void replacePhotos(java.util.List<UserPhoto> photos) {
        if (this.userPhotos == null) {
            this.userPhotos = new java.util.ArrayList<>();
        } else {
            this.userPhotos.clear();
        }
        if (photos != null) {
            for (UserPhoto p : photos) {
                p.setUser(this);
                this.userPhotos.add(p);
            }
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
