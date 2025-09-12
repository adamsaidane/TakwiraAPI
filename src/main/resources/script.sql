-- ============================================
-- Base de données
-- ============================================
CREATE DATABASE IF NOT EXISTS takwira;
USE takwira;

-- ============================================
-- Table des joueurs
-- ============================================
CREATE TABLE IF NOT EXISTS players
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
    );

-- Insertion de joueurs
INSERT INTO players (name)
VALUES ('Alice'), ('Bob'), ('Charlie'), ('David'), ('Eve'),
       ('Frank'), ('Grace'), ('Heidi'), ('Ivan'), ('Judy');

-- ============================================
-- Table des matchs
-- ============================================
CREATE TABLE IF NOT EXISTS matches
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(150) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
    );

-- Insertion de 5 matchs
INSERT INTO matches (title)
VALUES ('Match amical 1'), ('Match amical 2'), ('Match amical 3'),
       ('Match amical 4'), ('Match amical 5');

-- ============================================
-- Table PlayerMatch (remplace match_team1_players et match_team2_players)
-- ============================================
CREATE TABLE IF NOT EXISTS match_player
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id   BIGINT NOT NULL,
    player_id  BIGINT NOT NULL,
    team       ENUM('TEAM_1','TEAM_2') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id),
    UNIQUE (match_id, player_id)
    );

-- Insertion des joueurs dans les matchs avec enum TEAM_1 / TEAM_2
INSERT INTO match_player (match_id, player_id, team)
VALUES
    -- Match 1
    (1, 1, 'TEAM_1'),(1, 2, 'TEAM_1'),(1, 5, 'TEAM_1'),(1, 6, 'TEAM_1'),
    (1, 3, 'TEAM_2'),(1, 4, 'TEAM_2'),(1, 7, 'TEAM_2'),(1, 8, 'TEAM_2'),
    -- Match 2
    (2, 1, 'TEAM_1'),(2, 3, 'TEAM_1'),(2, 5, 'TEAM_1'),(2, 7, 'TEAM_1'),
    (2, 2, 'TEAM_2'),(2, 4, 'TEAM_2'),(2, 6, 'TEAM_2'),(2, 9, 'TEAM_2'),
    -- Match 3
    (3, 2, 'TEAM_1'),(3, 4, 'TEAM_1'),(3, 6, 'TEAM_1'),(3, 8, 'TEAM_1'),
    (3, 1, 'TEAM_2'),(3, 3, 'TEAM_2'),(3, 5, 'TEAM_2'),(3, 7, 'TEAM_2'),
    -- Match 4
    (4, 1, 'TEAM_1'),(4, 4, 'TEAM_1'),(4, 7, 'TEAM_1'),(4, 10, 'TEAM_1'),
    (4, 2, 'TEAM_2'),(4, 5, 'TEAM_2'),(4, 8, 'TEAM_2'),(4, 9, 'TEAM_2'),
    -- Match 5
    (5, 3, 'TEAM_1'),(5, 5, 'TEAM_1'),(5, 6, 'TEAM_1'),(5, 9, 'TEAM_1'),
    (5, 1, 'TEAM_2'),(5, 2, 'TEAM_2'),(5, 4, 'TEAM_2'),(5, 10, 'TEAM_2');
-- ============================================
-- Table des buts / passes décisives
-- ============================================
CREATE TABLE IF NOT EXISTS goals
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id   BIGINT NOT NULL,
    scorer_id  BIGINT NOT NULL,
    assist_id  BIGINT,
    team       ENUM ('TEAM_1','TEAM_2') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (scorer_id) REFERENCES players(id),
    FOREIGN KEY (assist_id) REFERENCES players(id)
    );

-- Insertion buts / passes
INSERT INTO goals (match_id, scorer_id, assist_id, team)
VALUES
    (1, 1, 2, 'TEAM_1'), (1, 3, NULL, 'TEAM_2'), (1, 5, 6, 'TEAM_1'),
    (2, 3, 1, 'TEAM_1'), (2, 4, 2, 'TEAM_2'), (2, 7, NULL, 'TEAM_1'),
    (3, 2, 8, 'TEAM_1'), (3, 5, NULL, 'TEAM_1'), (3, 1, 3, 'TEAM_2'),
    (4, 1, NULL, 'TEAM_1'), (4, 5, 2, 'TEAM_2'), (4, 10, 4, 'TEAM_1'),
    (5, 3, 5, 'TEAM_1'), (5, 4, NULL, 'TEAM_2'), (5, 9, 6, 'TEAM_1');
