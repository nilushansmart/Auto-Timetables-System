-- =====================================================================
-- School Timetable System - PostgreSQL schema (multi-tenant, 3NF)
-- V1__init.sql
-- =====================================================================

-- ---------- ENUM TYPES ----------
CREATE TYPE user_role        AS ENUM ('PLATFORM_ADMIN','SCHOOL_ADMIN','COORDINATOR','TEACHER');
CREATE TYPE school_type      AS ENUM ('GOV_1AB','GOV_1C','GOV_TYPE2','GOV_TYPE3','PRIVATE','INTERNATIONAL','PIRIVENA');
CREATE TYPE grade_level      AS ENUM ('PRIMARY','JUNIOR_SECONDARY','OL','AL');
CREATE TYPE slot_type        AS ENUM ('TEACHING','INTERVAL','ASSEMBLY','OTHER');
CREATE TYPE room_type        AS ENUM ('CLASSROOM','SCIENCE_LAB','ICT_LAB','HALL','GROUND','LIBRARY','ART_ROOM','OTHER');
CREATE TYPE availability     AS ENUM ('UNAVAILABLE','PREFERRED');
CREATE TYPE group_type       AS ENUM ('RELIGION','BASKET','STREAM_ELECTIVE','COMBINED');
CREATE TYPE version_status   AS ENUM ('DRAFT','PUBLISHED','ARCHIVED');
CREATE TYPE job_status       AS ENUM ('QUEUED','RUNNING','DONE','FAILED','CANCELLED');
CREATE TYPE calendar_kind    AS ENUM ('HOLIDAY','POYA','EXAM','SPORTS','EVENT','TERM_BREAK');

-- ---------- GLOBAL LOOKUP ----------
CREATE TABLE medium (
    id    SMALLINT PRIMARY KEY,
    code  VARCHAR(5)  NOT NULL UNIQUE,           -- SI, TA, EN
    name  VARCHAR(30) NOT NULL
);

-- ---------- TENANT ROOT ----------
CREATE TABLE school (
    id            BIGSERIAL PRIMARY KEY,
    census_no     VARCHAR(20) UNIQUE,            -- national school census number
    name          VARCHAR(200) NOT NULL,
    type          school_type  NOT NULL,
    province      VARCHAR(50),
    zone          VARCHAR(80),
    district      VARCHAR(50),
    timezone      VARCHAR(40) NOT NULL DEFAULT 'Asia/Colombo',
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------- ACCOUNTS ----------
CREATE TABLE teacher (
    id                 BIGSERIAL PRIMARY KEY,
    school_id          BIGINT NOT NULL REFERENCES school(id),
    staff_no           VARCHAR(30),
    full_name          VARCHAR(150) NOT NULL,
    short_code         VARCHAR(10),              -- e.g. "KP" for printed timetables
    email              VARCHAR(150),
    phone              VARCHAR(30),
    max_periods_week   SMALLINT,
    max_periods_day    SMALLINT,
    active             BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (school_id, staff_no),
    UNIQUE (school_id, short_code),
    UNIQUE (school_id, id)                       -- enables composite FKs
);

CREATE TABLE app_user (
    id             BIGSERIAL PRIMARY KEY,
    school_id      BIGINT REFERENCES school(id),             -- NULL only for PLATFORM_ADMIN
    teacher_id     BIGINT REFERENCES teacher(id),            -- link when role = TEACHER
    email          VARCHAR(150) NOT NULL UNIQUE,
    password_hash  VARCHAR(100) NOT NULL,
    full_name      VARCHAR(150) NOT NULL,
    role           user_role NOT NULL,
    active         BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at  TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK ((role = 'PLATFORM_ADMIN') = (school_id IS NULL))
);

-- ---------- CALENDAR ----------
CREATE TABLE academic_year (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id),
    year       SMALLINT NOT NULL,
    UNIQUE (school_id, year),
    UNIQUE (school_id, id)
);

CREATE TABLE term (
    id                BIGSERIAL PRIMARY KEY,
    school_id         BIGINT NOT NULL REFERENCES school(id),
    academic_year_id  BIGINT NOT NULL REFERENCES academic_year(id),
    term_no           SMALLINT NOT NULL CHECK (term_no BETWEEN 1 AND 3),
    start_date        DATE NOT NULL,
    end_date          DATE NOT NULL,
    CHECK (end_date > start_date),
    UNIQUE (academic_year_id, term_no),
    UNIQUE (school_id, id)
);

CREATE TABLE calendar_event (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id),
    event_date DATE NOT NULL,
    kind       calendar_kind NOT NULL,
    name       VARCHAR(150) NOT NULL
);

-- ---------- TIME GRID ----------
CREATE TABLE time_slot (
    id           BIGSERIAL PRIMARY KEY,
    school_id    BIGINT NOT NULL REFERENCES school(id),
    day_of_week  SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),  -- 1 = Monday
    period_no    SMALLINT NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    slot_type    slot_type NOT NULL DEFAULT 'TEACHING',
    CHECK (end_time > start_time),
    UNIQUE (school_id, day_of_week, period_no),
    UNIQUE (school_id, id)
);

-- ---------- ACADEMIC STRUCTURE ----------
CREATE TABLE grade (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id),
    grade_no   SMALLINT NOT NULL CHECK (grade_no BETWEEN 1 AND 13),
    level      grade_level NOT NULL,
    UNIQUE (school_id, grade_no),
    UNIQUE (school_id, id)
);

CREATE TABLE stream (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id),
    name       VARCHAR(50) NOT NULL,             -- Science, Commerce, Arts, Technology
    UNIQUE (school_id, name),
    UNIQUE (school_id, id)
);

CREATE TABLE class_group (
    id                BIGSERIAL PRIMARY KEY,
    school_id         BIGINT NOT NULL REFERENCES school(id),
    academic_year_id  BIGINT NOT NULL REFERENCES academic_year(id),
    grade_id          BIGINT NOT NULL REFERENCES grade(id),
    medium_id         SMALLINT NOT NULL REFERENCES medium(id),
    stream_id         BIGINT REFERENCES stream(id),
    name              VARCHAR(30) NOT NULL,      -- "6A", "12 Science"
    class_teacher_id  BIGINT REFERENCES teacher(id),
    student_count     SMALLINT,
    UNIQUE (academic_year_id, name),
    UNIQUE (school_id, id)
);

CREATE TABLE subject (
    id           BIGSERIAL PRIMARY KEY,
    school_id    BIGINT NOT NULL REFERENCES school(id),
    code         VARCHAR(20) NOT NULL,
    name_en      VARCHAR(100) NOT NULL,
    name_si      VARCHAR(100),
    name_ta      VARCHAR(100),
    is_religion  BOOLEAN NOT NULL DEFAULT FALSE,
    is_practical BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (school_id, code),
    UNIQUE (school_id, id)
);

CREATE TABLE teacher_subject (
    teacher_id  BIGINT NOT NULL REFERENCES teacher(id) ON DELETE CASCADE,
    subject_id  BIGINT NOT NULL REFERENCES subject(id) ON DELETE CASCADE,
    medium_id   SMALLINT NOT NULL REFERENCES medium(id),
    PRIMARY KEY (teacher_id, subject_id, medium_id)
);

CREATE TABLE teacher_availability (
    teacher_id    BIGINT NOT NULL REFERENCES teacher(id) ON DELETE CASCADE,
    time_slot_id  BIGINT NOT NULL REFERENCES time_slot(id) ON DELETE CASCADE,
    kind          availability NOT NULL,
    PRIMARY KEY (teacher_id, time_slot_id)
);

CREATE TABLE room (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id),
    name       VARCHAR(60) NOT NULL,
    type       room_type NOT NULL DEFAULT 'CLASSROOM',
    capacity   SMALLINT,
    UNIQUE (school_id, name),
    UNIQUE (school_id, id)
);

CREATE TABLE subject_basket (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id),
    grade_id   BIGINT NOT NULL REFERENCES grade(id),
    basket_no  SMALLINT NOT NULL,                -- Basket 1, 2, 3
    UNIQUE (grade_id, basket_no)
);

CREATE TABLE basket_subject (
    basket_id   BIGINT NOT NULL REFERENCES subject_basket(id) ON DELETE CASCADE,
    subject_id  BIGINT NOT NULL REFERENCES subject(id) ON DELETE CASCADE,
    PRIMARY KEY (basket_id, subject_id)
);

-- ---------- LESSONS (solver input) ----------
CREATE TABLE lesson_group (
    id                BIGSERIAL PRIMARY KEY,
    school_id         BIGINT NOT NULL REFERENCES school(id),
    academic_year_id  BIGINT NOT NULL REFERENCES academic_year(id),
    type              group_type NOT NULL,
    name              VARCHAR(100) NOT NULL,
    UNIQUE (school_id, id)
);

CREATE TABLE lesson (
    id                BIGSERIAL PRIMARY KEY,
    school_id         BIGINT NOT NULL REFERENCES school(id),
    academic_year_id  BIGINT NOT NULL REFERENCES academic_year(id),
    subject_id        BIGINT NOT NULL REFERENCES subject(id),
    teacher_id        BIGINT NOT NULL REFERENCES teacher(id),
    lesson_group_id   BIGINT REFERENCES lesson_group(id),
    required_room_type room_type,                -- e.g. SCIENCE_LAB
    fixed_room_id     BIGINT REFERENCES room(id),
    periods_per_week  SMALLINT NOT NULL CHECK (periods_per_week > 0),
    block_size        SMALLINT NOT NULL DEFAULT 1 CHECK (block_size BETWEEN 1 AND 3),  -- 2 = double period
    max_per_day       SMALLINT NOT NULL DEFAULT 2,
    UNIQUE (school_id, id)
);

CREATE TABLE lesson_class (
    lesson_id       BIGINT NOT NULL REFERENCES lesson(id) ON DELETE CASCADE,
    class_group_id  BIGINT NOT NULL REFERENCES class_group(id),
    student_count   SMALLINT,                    -- subset size for split groups
    PRIMARY KEY (lesson_id, class_group_id)
);

-- ---------- CONSTRAINT CONFIGURATION ----------
CREATE TABLE constraint_config (
    id              BIGSERIAL PRIMARY KEY,
    school_id       BIGINT NOT NULL REFERENCES school(id),
    constraint_code VARCHAR(60) NOT NULL,
    is_hard         BOOLEAN NOT NULL DEFAULT FALSE,
    weight          INTEGER NOT NULL DEFAULT 1,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    params          JSONB,
    UNIQUE (school_id, constraint_code)
);

-- ---------- SOLVING & TIMETABLES ----------
CREATE TABLE solve_job (
    id               BIGSERIAL PRIMARY KEY,
    school_id        BIGINT NOT NULL REFERENCES school(id),
    term_id          BIGINT NOT NULL REFERENCES term(id),
    requested_by     BIGINT REFERENCES app_user(id),
    status           job_status NOT NULL DEFAULT 'QUEUED',
    time_limit_sec   INTEGER NOT NULL DEFAULT 120,
    best_hard_score  INTEGER,
    best_soft_score  INTEGER,
    error_message    TEXT,
    queued_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    started_at       TIMESTAMPTZ,
    finished_at      TIMESTAMPTZ,
    UNIQUE (school_id, id)
);

CREATE TABLE timetable_version (
    id           BIGSERIAL PRIMARY KEY,
    school_id    BIGINT NOT NULL REFERENCES school(id),
    term_id      BIGINT NOT NULL REFERENCES term(id),
    solve_job_id BIGINT REFERENCES solve_job(id),
    name         VARCHAR(100) NOT NULL,
    status       version_status NOT NULL DEFAULT 'DRAFT',
    hard_score   INTEGER,
    soft_score   INTEGER,
    created_by   BIGINT REFERENCES app_user(id),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ,
    UNIQUE (school_id, id)
);

CREATE UNIQUE INDEX one_published_per_term
    ON timetable_version (school_id, term_id) WHERE status = 'PUBLISHED';

CREATE TABLE timetable_entry (
    id            BIGSERIAL PRIMARY KEY,
    school_id     BIGINT NOT NULL REFERENCES school(id),
    version_id    BIGINT NOT NULL REFERENCES timetable_version(id) ON DELETE CASCADE,
    lesson_id     BIGINT NOT NULL REFERENCES lesson(id),
    time_slot_id  BIGINT NOT NULL REFERENCES time_slot(id),
    room_id       BIGINT REFERENCES room(id),
    is_pinned     BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (version_id, lesson_id, time_slot_id)
);
CREATE INDEX idx_entry_version_slot ON timetable_entry (version_id, time_slot_id);
CREATE INDEX idx_entry_lesson       ON timetable_entry (lesson_id);

-- ---------- DAILY OPERATIONS (later phase) ----------
CREATE TABLE teacher_absence (
    id          BIGSERIAL PRIMARY KEY,
    school_id   BIGINT NOT NULL REFERENCES school(id),
    teacher_id  BIGINT NOT NULL REFERENCES teacher(id),
    absent_date DATE NOT NULL,
    reason      VARCHAR(200),
    UNIQUE (teacher_id, absent_date)
);

CREATE TABLE substitution (
    id                  BIGSERIAL PRIMARY KEY,
    school_id           BIGINT NOT NULL REFERENCES school(id),
    absence_id          BIGINT NOT NULL REFERENCES teacher_absence(id) ON DELETE CASCADE,
    timetable_entry_id  BIGINT NOT NULL REFERENCES timetable_entry(id),
    substitute_teacher_id BIGINT NOT NULL REFERENCES teacher(id),
    UNIQUE (absence_id, timetable_entry_id)
);

CREATE TABLE audit_log (
    id          BIGSERIAL PRIMARY KEY,
    school_id   BIGINT REFERENCES school(id),
    user_id     BIGINT REFERENCES app_user(id),
    action      VARCHAR(60) NOT NULL,
    entity      VARCHAR(60),
    entity_id   BIGINT,
    detail      JSONB,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------- USEFUL INDEXES ----------
CREATE INDEX idx_teacher_school   ON teacher (school_id);
CREATE INDEX idx_class_year       ON class_group (academic_year_id, grade_id);
CREATE INDEX idx_lesson_year      ON lesson (academic_year_id);
CREATE INDEX idx_lesson_teacher   ON lesson (teacher_id);
CREATE INDEX idx_lesson_group     ON lesson (lesson_group_id);
CREATE INDEX idx_job_status       ON solve_job (status, queued_at);

-- ---------- ROW-LEVEL SECURITY ----------

-- App user tenant isolation (permits login check when app.school_id is not yet set)
ALTER TABLE app_user ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_user FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON app_user
    FOR ALL
    USING (current_setting('app.school_id', true) IS NULL OR current_setting('app.school_id', true) = '' OR school_id = current_setting('app.school_id', true)::BIGINT)
    WITH CHECK (current_setting('app.school_id', true) IS NULL OR current_setting('app.school_id', true) = '' OR school_id = current_setting('app.school_id', true)::BIGINT);

-- Direct tenant tables
DO $$
DECLARE
    tbl text;
BEGIN
    FOR tbl IN
        SELECT unnest(ARRAY[
            'teacher', 'academic_year', 'term', 'calendar_event', 'time_slot',
            'grade', 'stream', 'class_group', 'subject', 'room',
            'subject_basket', 'lesson_group', 'lesson', 'constraint_config',
            'solve_job', 'timetable_version', 'timetable_entry',
            'teacher_absence', 'substitution', 'audit_log'
        ])
    LOOP
        EXECUTE format('ALTER TABLE %I ENABLE ROW LEVEL SECURITY', tbl);
        EXECUTE format('ALTER TABLE %I FORCE ROW LEVEL SECURITY', tbl);
        EXECUTE format('CREATE POLICY tenant_isolation ON %I FOR ALL USING (school_id = current_setting(''app.school_id'', true)::BIGINT) WITH CHECK (school_id = current_setting(''app.school_id'', true)::BIGINT)', tbl);
    END LOOP;
END $$;

-- Associated join tables without school_id column
ALTER TABLE teacher_subject ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_subject FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON teacher_subject
    FOR ALL
    USING (EXISTS (SELECT 1 FROM teacher t WHERE t.id = teacher_id))
    WITH CHECK (EXISTS (SELECT 1 FROM teacher t WHERE t.id = teacher_id));

ALTER TABLE teacher_availability ENABLE ROW LEVEL SECURITY;
ALTER TABLE teacher_availability FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON teacher_availability
    FOR ALL
    USING (EXISTS (SELECT 1 FROM teacher t WHERE t.id = teacher_id))
    WITH CHECK (EXISTS (SELECT 1 FROM teacher t WHERE t.id = teacher_id));

ALTER TABLE lesson_class ENABLE ROW LEVEL SECURITY;
ALTER TABLE lesson_class FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON lesson_class
    FOR ALL
    USING (EXISTS (SELECT 1 FROM lesson l WHERE l.id = lesson_id))
    WITH CHECK (EXISTS (SELECT 1 FROM lesson l WHERE l.id = lesson_id));

ALTER TABLE basket_subject ENABLE ROW LEVEL SECURITY;
ALTER TABLE basket_subject FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON basket_subject
    FOR ALL
    USING (EXISTS (SELECT 1 FROM subject_basket b WHERE b.id = basket_id))
    WITH CHECK (EXISTS (SELECT 1 FROM subject_basket b WHERE b.id = basket_id));

-- ---------- GLOBAL SEED ----------
INSERT INTO medium (id, code, name) VALUES
(1,'SI','Sinhala'),
(2,'TA','Tamil'),
(3,'EN','English');
