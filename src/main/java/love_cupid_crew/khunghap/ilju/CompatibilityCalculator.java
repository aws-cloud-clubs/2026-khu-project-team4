package love_cupid_crew.khunghap.ilju;

import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;

import java.util.Map;
import java.util.Set;

public class CompatibilityCalculator {

    // 오행 상생: key가 value를 생(生)함
    private static final Map<String, String> SAENGSAN = Map.of(
            "목", "화", "화", "토", "토", "금", "금", "수", "수", "목"
    );

    // 천간합: 갑기 / 을경 / 병신 / 정임 / 무계
    private static final Set<String> CHEONGAN_HAP = Set.of(
            "갑기", "기갑", "을경", "경을", "병신", "신병", "정임", "임정", "무계", "계무"
    );

    // 지지 육합: 자축 / 인해 / 묘술 / 진유 / 사신 / 오미
    private static final Set<String> JIJI_YUKHAP = Set.of(
            "자축", "축자", "인해", "해인", "묘술", "술묘",
            "진유", "유진", "사신", "신사", "오미", "미오"
    );

    // 지지 삼합 쌍: 인오술 / 해묘미 / 신자진 / 사유축
    private static final Set<String> JIJI_SAMHAP = Set.of(
            "인오", "오인", "오술", "술오", "인술", "술인",
            "해묘", "묘해", "묘미", "미묘", "해미", "미해",
            "신자", "자신", "자진", "진자", "신진", "진신",
            "사유", "유사", "유축", "축유", "사축", "축사"
    );

    // 지지 충: 자오 / 축미 / 인신 / 묘유 / 진술 / 사해
    private static final Set<String> JIJI_CHUNG = Set.of(
            "자오", "오자", "축미", "미축", "인신", "신인",
            "묘유", "유묘", "진술", "술진", "사해", "해사"
    );

    public record Result(short score, String ohengRelation) {}

    /**
     * 점수 구성
     *   오행 베이스: 상생 55 / 비화 45 / 상극 25
     *   천간합 보너스: +15
     *   지지 육합: +15 / 삼합 쌍: +10 / 충: -15
     *   최종 범위: 0~100 (clamp)
     *
     * 예시 분포
     *   상생 + 천간합 + 지지육합 = 85
     *   상생 + 천간합 or 지지육합 = 70
     *   상생 + 지지삼합            = 65
     *   상생                       = 55
     *   비화 + 천간합 + 지지육합   = 75
     *   비화                       = 45
     *   상극 + 천간합 + 지지육합   = 55
     *   상극 + 천간합              = 40
     *   상극                       = 25
     *   상극 + 지지충              = 10
     */
    public static Result calculate(IljuAnimal a, IljuAnimal b) {
        String aOheng = a.getOheng();
        String bOheng = b.getOheng();

        int base;
        String ohengRelation;
        if (aOheng.equals(bOheng)) {
            base = 45;
            ohengRelation = "같은 " + aOheng + "의 동질감";
        } else if (bOheng.equals(SAENGSAN.get(aOheng)) || aOheng.equals(SAENGSAN.get(bOheng))) {
            base = 55;
            ohengRelation = aOheng + "과 " + bOheng + "의 조화";
        } else {
            base = 25;
            ohengRelation = aOheng + "과 " + bOheng + "의 긴장";
        }

        String cheonganPair = a.getCheongan() + b.getCheongan();
        int cheonganBonus = CHEONGAN_HAP.contains(cheonganPair) ? 15 : 0;

        String jijiPair = a.getJiji() + b.getJiji();
        int jijiModifier;
        if (JIJI_CHUNG.contains(jijiPair)) {
            jijiModifier = -15;
        } else if (JIJI_YUKHAP.contains(jijiPair)) {
            jijiModifier = 15;
        } else if (JIJI_SAMHAP.contains(jijiPair)) {
            jijiModifier = 10;
        } else {
            jijiModifier = 0;
        }

        int total = Math.clamp(base + cheonganBonus + jijiModifier, 0, 100);
        return new Result((short) total, ohengRelation);
    }
}
