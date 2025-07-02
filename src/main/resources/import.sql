INSERT INTO `user` (is_open, created_at, deleted_at, id, updated_at,nickname, email, password, main_cover_url, profile_image, user_public_id, account_type, role, social_type, introduction) VALUES (0, NULL, NULL, 1, NULL,'user_1', 'test@naver.com', '{bcrypt}$2a$10$bfenGyjxjsLZ2jQDZUlYeOMdK/sAbmuPBJhiDaCmEkvBa84nzJer2', NULL, NULL,'user_public_id_1', 'FAN', 'USER', 'GOOGLE', NULL);
INSERT INTO `user` (is_open, created_at, deleted_at, id, updated_at,nickname, email, password, main_cover_url, profile_image,user_public_id, account_type, role, social_type, introduction) VALUES (0, NULL, NULL, 2, NULL,'user_2', 'test2@naver.com', '{bcrypt}$2a$10$bfenGyjxjsLZ2jQDZUlYeOMdK/sAbmuPBJhiDaCmEkvBa84nzJer2', NULL, NULL,'user_public_id_2', 'ARTIST', 'ARTIST', 'GOOGLE', NULL);


INSERT INTO `music_session` (artist_id, closed_at, id, started_at, status) VALUES (1, NULL, 1, NOW(), 'OPEN');

INSERT INTO `music_session` (artist_id, closed_at, id, started_at, status) VALUES (2, NOW(), 2, NOW() - INTERVAL 1 HOUR, 'CLOSE');

INSERT INTO `song_request` (requested_at, session_id, music_platform_name, fan_email,platform_track_id, song_artist_name, song_title) VALUES (NOW(), 1, 'Spotify', 'fan1@example.com', 'track_001', 'BTS', 'Dynamite');
INSERT INTO `song_request` (requested_at, session_id, music_platform_name, fan_email,platform_track_id, song_artist_name, song_title) VALUES (NOW() - INTERVAL 1 DAY, 1, 'Spotify', 'fan2@example.com', 'track_002', 'BLACKPINK', 'How You Like That');
INSERT INTO `song_request` (requested_at, session_id, music_platform_name, fan_email,platform_track_id, song_artist_name, song_title) VALUES (NOW() - INTERVAL 2 DAY, 2, 'Spotify', 'fan3@example.com', 'track_003', 'IU', 'Blueming');
INSERT INTO `song_request` (requested_at, session_id, music_platform_name, fan_email,platform_track_id, song_artist_name, song_title) VALUES (NOW() - INTERVAL 3 DAY, 2, 'Spotify', 'fan4@example.com', 'track_004', '지코(ZICO)', '아무노래');
