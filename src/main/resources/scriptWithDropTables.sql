-- ============================================
-- Base de données
-- ============================================
CREATE DATABASE IF NOT EXISTS takwira;
USE takwira;

-- ============================================
-- Table des joueurs
-- ============================================
DROP TABLE IF EXISTS match_team1_players;
DROP TABLE IF EXISTS match_team2_players;
DROP TABLE IF EXISTS goals;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS players;

CREATE TABLE IF NOT EXISTS players
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
);

-- Insertion de joueurs
INSERT INTO players (name)
VALUES ('Alice'),
       ('Bob'),
       ('Charlie'),
       ('David'),
       ('Eve'),
       ('Frank'),
       ('Grace'),
       ('Heidi'),
       ('Ivan'),
       ('Judy');

-- ============================================
-- Table des matchs
-- ============================================
CREATE TABLE IF NOT EXISTS matches
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(150) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
);

-- Insertion de 5 matchs
INSERT INTO matches (title)
VALUES ('Match amical 1'),
       ('Match amical 2'),
       ('Match amical 3'),
       ('Match amical 4'),
       ('Match amical 5');

-- ============================================
-- Table de composition Team1
-- ============================================
CREATE TABLE IF NOT EXISTS match_team1_players
(
    match_id   BIGINT   NOT NULL,
    player_id  BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN  NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    PRIMARY KEY (match_id, player_id),
    FOREIGN KEY (match_id) REFERENCES matches (id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players (id)
);

-- Insertion Team1
INSERT INTO match_team1_players (match_id, player_id)
VALUES (1, 1),
       (1, 2),
       (1, 5),
       (1, 6),
       (2, 1),
       (2, 3),
       (2, 5),
       (2, 7),
       (3, 2),
       (3, 4),
       (3, 6),
       (3, 8),
       (4, 1),
       (4, 4),
       (4, 7),
       (4, 10),
       (5, 3),
       (5, 5),
       (5, 6),
       (5, 9);

-- ============================================
-- Table de composition Team2
-- ============================================
CREATE TABLE IF NOT EXISTS match_team2_players
(
    match_id   BIGINT   NOT NULL,
    player_id  BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN  NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    PRIMARY KEY (match_id, player_id),
    FOREIGN KEY (match_id) REFERENCES matches (id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players (id)
);

-- Insertion Team2
INSERT INTO match_team2_players (match_id, player_id)
VALUES (1, 3),
       (1, 4),
       (1, 7),
       (1, 8),
       (2, 2),
       (2, 4),
       (2, 6),
       (2, 9),
       (3, 1),
       (3, 3),
       (3, 5),
       (3, 7),
       (4, 2),
       (4, 5),
       (4, 8),
       (4, 9),
       (5, 1),
       (5, 2),
       (5, 4),
       (5, 10);

-- ============================================
-- Table des buts / passes décisives
-- ============================================
CREATE TABLE IF NOT EXISTS goals
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id   BIGINT                   NOT NULL,
    scorer_id  BIGINT                   NOT NULL,
    assist_id  BIGINT,
    team       ENUM ('TEAM_1','TEAM_2') NOT NULL,
    created_at DATETIME                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    FOREIGN KEY (match_id) REFERENCES matches (id) ON DELETE CASCADE,
    FOREIGN KEY (scorer_id) REFERENCES players (id),
    FOREIGN KEY (assist_id) REFERENCES players (id)
);

-- Insertion buts / passes
INSERT INTO goals (match_id, scorer_id, assist_id, team)
VALUES (1, 1, 2, 'TEAM_1'),
       (1, 3, NULL, 'TEAM_2'),
       (1, 5, 6, 'TEAM_1'),
       (2, 3, 1, 'TEAM_1'),
       (2, 4, 2, 'TEAM_2'),
       (2, 7, NULL, 'TEAM_1'),
       (3, 2, 8, 'TEAM_1'),
       (3, 5, NULL, 'TEAM_1'),
       (3, 1, 3, 'TEAM_2'),
       (4, 1, NULL, 'TEAM_1'),
       (4, 5, 2, 'TEAM_2'),
       (4, 10, 4, 'TEAM_1'),
       (5, 3, 5, 'TEAM_1'),
       (5, 4, NULL, 'TEAM_2'),
       (5, 9, 6, 'TEAM_1');
