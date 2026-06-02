package love_cupid_crew.khunghap.ilju;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import love_cupid_crew.khunghap.ilju.repository.IljuAnimalRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class IljuCalculator {

    private final IljuAnimalRepository iljuAnimalRepository;

    // 기준: 1900-01-01 = 甲子日 (갑자), epochDay = -25567
    // 검증: (-25567 + 7) % 60 = -25560 % 60 = 0 → 甲(0) 子(0)
    private static final int GANZHI_OFFSET = 7;

    private static final String[] CHEONGAN = {"갑", "을", "병", "정", "무", "기", "경", "신", "임", "계"};
    private static final String[] JIJI     = {"자", "축", "인", "묘", "진", "사", "오", "미", "신", "유", "술", "해"};

    public IljuAnimal calculate(LocalDate birthDate) {
        long epochDay = birthDate.toEpochDay();
        int ganzhiPos = (int)(((epochDay + GANZHI_OFFSET) % 60 + 60) % 60);

        String cheongan = CHEONGAN[ganzhiPos % 10];
        String jiji     = JIJI[ganzhiPos % 12];

        return iljuAnimalRepository.findByCheonganAndJiji(cheongan, jiji)
                .orElseThrow(() -> new IllegalStateException(
                        "일주동물 데이터를 찾을 수 없습니다: 천간=" + cheongan + ", 지지=" + jiji));
    }
}