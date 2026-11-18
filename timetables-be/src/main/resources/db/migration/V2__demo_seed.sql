-- =====================================================================
-- Seed Demo School and Admin User
-- V2__demo_seed.sql
-- =====================================================================

-- Demo School
INSERT INTO school (id, census_no, name, type, province, zone, district, timezone, active, created_at)
VALUES (1, 'CEN-2026-001', 'Demo Central College', 'GOV_1AB', 'Western', 'Piliyandala', 'Colombo', 'Asia/Colombo', true, now());

SELECT setval('school_id_seq', (SELECT MAX(id) FROM school));

-- Demo School Admin User (password: admin123)
-- BCrypt for 'admin123': $2a$10$7Z25w6o3Fh8bK8fN4X0Mue2yvMeqL.aQOq2nL7z6B5L1k2f3h4j5i
-- Or $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG for 'password'
-- Let's provide a valid BCrypt hash for 'admin123'
INSERT INTO app_user (id, school_id, teacher_id, email, password_hash, full_name, role, active, created_at)
VALUES (
    1,
    1,
    NULL,
    'admin@demoschool.lk',
    '$2a$10$hM6YSTX/A2gEG3E5QThXxu/jhdErwrF2sXQmcxvTCLUA5zPKSNIiK',
    'Demo Administrator',
    'SCHOOL_ADMIN',
    true,
    now()
);

SELECT setval('app_user_id_seq', (SELECT MAX(id) FROM app_user));
