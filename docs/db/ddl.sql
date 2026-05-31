-- =============================================
-- 쿵합 (KhungHap) DDL
-- PostgreSQL
-- =============================================


-- =============================================
-- ILJU_ANIMAL  (일주동물 레퍼런스)
-- =============================================
CREATE TABLE ilju_animal (
                             id          SERIAL      PRIMARY KEY,
                             name        VARCHAR(30) NOT NULL UNIQUE,
                             emoji       VARCHAR(10) NOT NULL,
                             cheongan    VARCHAR(5)  NOT NULL,
                             jiji        VARCHAR(5)  NOT NULL,
                             oheng       VARCHAR(5)  NOT NULL,
                             description TEXT,
                             personality TEXT[],
                             strengths   TEXT[],
                             weaknesses  TEXT[]
);

-- =============================================
-- USER
-- =============================================
CREATE TABLE "user" (
                        id              BIGSERIAL   PRIMARY KEY,
                        email           VARCHAR(100) NOT NULL UNIQUE,
                        password        VARCHAR(100) NOT NULL,
                        name            VARCHAR(50)  NOT NULL,
                        nickname        VARCHAR(50)  NOT NULL,
                        phone           VARCHAR(20)  NOT NULL,
                        birth_date      DATE         NOT NULL,
                        birth_hour      VARCHAR(20),                        -- 오시, 자시 등 (모를 수도 있음)
                        gender          VARCHAR(10)  NOT NULL,              -- MALE / FEMALE
                        college         VARCHAR(100) NOT NULL,
                        ilju_animal_id  INT          NOT NULL REFERENCES ilju_animal(id),
                        is_active       BOOLEAN      NOT NULL DEFAULT TRUE, -- 매칭 활성화 여부
                        created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- =============================================
-- USER_PHOTO
-- =============================================
CREATE TABLE user_photo (
                            id              BIGSERIAL   PRIMARY KEY,
                            user_id         BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                            image_url       TEXT        NOT NULL,
                            display_order   SMALLINT    NOT NULL CHECK (display_order BETWEEN 1 AND 5),
                            UNIQUE (user_id, display_order)
);

-- =============================================
-- USER_LIFESTYLE
-- =============================================
CREATE TABLE user_lifestyle (
                                id              BIGSERIAL   PRIMARY KEY,
                                user_id         BIGINT      NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
                                emoji_intro     VARCHAR(50),                        -- 이모티콘만, 최대 10개
                                mbti            VARCHAR(4),
                                drinking        VARCHAR(20) NOT NULL,               -- NEVER / SOMETIMES / OFTEN
                                smoking         VARCHAR(20) NOT NULL                -- NON_SMOKER / SMOKER
);

-- =============================================
-- USER_HOBBY
-- =============================================
CREATE TABLE user_hobby (
                            id              BIGSERIAL   PRIMARY KEY,
                            user_id         BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                            hobby           VARCHAR(30) NOT NULL,
                            UNIQUE (user_id, hobby)
);

-- =============================================
-- USER_DATING_STYLE
-- =============================================
CREATE TABLE user_dating_style (
                                   id              BIGSERIAL   PRIMARY KEY,
                                   user_id         BIGINT      NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
                                   purpose         VARCHAR(30) NOT NULL,               -- SERIOUS / CASUAL / MARRIAGE / UNSURE
                                   contact_style   VARCHAR(20) NOT NULL,               -- FREQUENT / NORMAL / AS_NEEDED
                                   date_style      VARCHAR(20) NOT NULL,               -- ACTIVE / QUIET / MIXED
                                   personal_time   VARCHAR(20) NOT NULL,               -- IMPORTANT / TOGETHER / MIXED
                                   age_min         SMALLINT,
                                   age_max         SMALLINT,
                                   height_min      SMALLINT,
                                   height_max      SMALLINT,
                                   same_college_excluded  BOOLEAN
);

-- =============================================
-- USER_PREFERENCE  (흡연/음주/반려동물/운동 선호도)
-- =============================================
CREATE TABLE user_preference (
                                 id              BIGSERIAL   PRIMARY KEY,
                                 user_id         BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                                 category        VARCHAR(30) NOT NULL,               -- SMOKING / DRINKING / PET / EXERCISE
                                 score           VARCHAR(20) NOT NULL,               -- VERY_BAD / BAD / NEUTRAL / GOOD / VERY_GOOD
                                 UNIQUE (user_id, category)
);

-- =============================================
-- MATCH_CANDIDATE  (오늘의 인연 추천)
-- =============================================
CREATE TABLE match_candidate (
                                 id                  BIGSERIAL   PRIMARY KEY,
                                 user_id             BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                                 candidate_id        BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                                 compatibility_score SMALLINT    NOT NULL CHECK (compatibility_score BETWEEN 0 AND 100),
                                 oheng_relation      VARCHAR(50),                    -- e.g. 화(火)와 수(水)의 조화
                                 created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 UNIQUE (user_id, candidate_id)
);

-- =============================================
-- CHAT_ROOM
-- =============================================
CREATE TABLE chat_room (
                           id              BIGSERIAL   PRIMARY KEY,
                           user_a_id       BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                           user_b_id       BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                           message_count   INT         NOT NULL DEFAULT 0 CHECK (message_count >= 0),
                           status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / COMPLETED / EXPIRED
                           created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           expires_at      TIMESTAMPTZ,
                           CHECK (user_a_id <> user_b_id)
);

-- =============================================
-- CHAT_MESSAGE
-- =============================================
CREATE TABLE chat_message (
                              id              BIGSERIAL   PRIMARY KEY,
                              room_id         BIGINT      NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
                              sender_id       BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                              content         TEXT        NOT NULL,
                              sent_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =============================================
-- CHOICE_REPORT  (150번 후 정보 공개 요청)
-- =============================================
CREATE TABLE choice_report (
                               id            BIGSERIAL PRIMARY KEY,

    -- 채팅방당 1개만 생성되도록 UNIQUE 보장
                               room_id       BIGINT NOT NULL UNIQUE REFERENCES chat_room(id) ON DELETE CASCADE,

    -- chat_room.user_a_id / user_b_id 와 1:1 대응
    -- NULL : 아직 미응답 / YES : 공개 동의 / NO : 공개 거부
                               user_a_choice VARCHAR(10) CHECK (user_a_choice IN ('YES', 'NO')),
                               user_b_choice VARCHAR(10) CHECK (user_b_choice IN ('YES', 'NO')),

    -- WAITING  : 한 명 이상 아직 미응답
    -- REVEALED : 둘 다 YES → 실명/전화번호 공개
    -- REJECTED : 한 명이라도 NO 또는 24시간 만료
                               result        VARCHAR(20) NOT NULL DEFAULT 'WAITING'
                                   CHECK (result IN ('WAITING', 'REVEALED', 'REJECTED')),

    -- 채팅방이 막힐 때 각자 상대방에게 남기는 마지막 메시지
                               user_a_last_message  TEXT,
                               user_b_last_message  TEXT,

                               created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- 생성 시점 기준 24시간 후 자동 만료 (스케줄러가 이 값 기준으로 REJECTED 처리)
                               expires_at    TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '24 hours',

    -- REVEALED / REJECTED 확정 시각, WAITING 동안은 NULL
                               resolved_at   TIMESTAMPTZ
);

-- =============================================
-- REPORT
-- =============================================
CREATE TABLE report (
                        id              BIGSERIAL   PRIMARY KEY,
                        reporter_id     BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                        reported_id     BIGINT      NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                        category        VARCHAR(50) NOT NULL,               -- ABUSE / INAPPROPRIATE_PHOTO / PERSONAL_INFO / SPAM / ETC
                        description     TEXT,
                        status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING / REVIEWED / RESOLVED
                        created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                        CHECK (reporter_id <> reported_id)
);

-- =============================================
-- COIN_BALANCE
-- =============================================
CREATE TABLE coin_balance (
                              id              BIGSERIAL   PRIMARY KEY,
                              user_id         BIGINT      NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
                              balance         INT         NOT NULL DEFAULT 0 CHECK (balance >= 0),
                              updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =============================================
-- INDEX
-- =============================================
CREATE INDEX idx_user_photo_user_id         ON user_photo(user_id);
CREATE INDEX idx_user_hobby_user_id         ON user_hobby(user_id);
CREATE INDEX idx_user_preference_user_id    ON user_preference(user_id);
CREATE INDEX idx_match_candidate_user_id    ON match_candidate(user_id);
CREATE INDEX idx_match_candidate_created    ON match_candidate(created_at DESC);
CREATE INDEX idx_chat_room_user_a           ON chat_room(user_a_id);
CREATE INDEX idx_chat_room_user_b           ON chat_room(user_b_id);
CREATE INDEX idx_chat_message_room_id       ON chat_message(room_id);
CREATE INDEX idx_chat_message_sent_at       ON chat_message(sent_at DESC);
CREATE INDEX idx_report_reported_id         ON report(reported_id);