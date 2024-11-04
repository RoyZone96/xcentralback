-- Migration to create xcentral database
CREATE DATABASE IF NOT EXISTS xcentral;
USE xcentral;


-- Migration to add columns for id and name in blades table
-- Migration to create blades table
CREATE TABLE blades_parts IF NOT EXISTS xcentral(
    id INT PRIMARY KEY AUTO_INCREMENT,
    blade_name VARCHAR(255)
);


-- Migration to insert predefined blade names into blades_parts table

-- INSERT INTO blades (id, name) VALUES
-- (1, 'Black Shell'),
-- (2, 'Cobalt Dragoon'),
-- (3, 'Cobalt Drake'),
-- (4, 'Dran Dagger'),
-- (5, 'Dran Sword'),
-- (6, 'Hells Chain'),
-- (7, 'Hells Scythe'),
-- (8, 'Knight Shield'),
-- (9, 'Leon Claw'),
-- (10, 'Phoenix Wing'),
-- (11, 'Rhino Horn'),
-- (12, 'Shark Edge'),
-- (13, 'Shinobi Shadow'),
-- (14, 'Sphinx Cowl'),
-- (15, 'Tyranno Beat'),
-- (16, 'Unicorn Sting'),
-- (17, 'Viper Tail'),
-- (18, 'Weiss Tiger'),
-- (19, 'Wizard Arrow'),
-- (20, 'Wyvern Gale'),
-- (21, 'Dran Buster'),
-- (22, 'Hells Hammer'),
-- (23, 'Wizard Rod'),
-- (24, 'Leon Crest'),
-- (25, 'Phoenix Rudder'),
-- (26, 'Whale Wave');

-- Migration to create ratchets table
CREATE TABLE ratchet IF NOT EXISTS xcentral(
    id INT PRIMARY KEY AUTO_INCREMENT,
    ratchet VARCHAR(255)
);
-- INSERT INTO ratchet (id, ratchet) VALUES
-- ('1', '2-60'),
-- ('2', '2-80'),
-- ('3', '3-60'),
-- ('4', '3-80'),
-- ('5', '4-60'),
-- ('6', '4-80'),
-- ('7', '4-70'),
-- ('8', '5-60'),
-- ('9', '5-80'),
-- ('10', '9-60'),
-- ('11', '9-80'),
-- ('12', '1-60'),
-- ('13', '3-70'),
-- ('14', '5-70');

-- Migration to create bits table
CREATE TABLE bittype IF NOT EXISTS xcentral(
    id INT PRIMARY KEY AUTO_INCREMENT,
    bit VARCHAR(255)
);


-- Migration to insert predefined bit types into bittype table
-- INSERT INTO bittype (id, bit) VALUES
-- ('1', 'Ball'),
-- ('2', 'Cylcone'),
-- ('3', 'Dot'),
-- ('4', 'Flat'),
-- ('5', 'Gear Ball'),
-- ('6', 'Gear Flat'),
-- ('7', 'Gear Needle'),
-- ('8', 'Gear Point'),
-- ('9', 'High Needle'),
-- ('10', 'High Taper'),
-- ('11', 'Low Flat'),
-- ('12', 'Needle'),
-- ('13', 'Orb'),
-- ('14', 'Point'),
-- ('15', 'Quake'),
-- ('16', 'Spike'),
-- ('17', 'Taper'),
-- ('18', 'Unite'),
-- ('19', 'Rush'),
-- ('20', 'Metal Needle'),
-- ('21', 'Accel'),
-- ('22', 'Hexa'),
-- ('23', 'Disc Ball');

CREATE TABLE users IF NOT EXISTS xcentral(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
);


CREATE TABLE submission IF NOT EXISTS xcentral(
     submission_id INT PRIMARY KEY AUTO_INCREMENT,
     blade VARCHAR(50) NOT NULL,
     ratchet VARCHAR(50) NOT NULL,
     bit VARCHAR(50) NOT NULL,
     wins INT NOT NULL DEFAULT 0,
     losses INT NOT NULL DEFAULT 0,
     win_rate_avg DECIMAL(5, 2) GENERATED ALWAYS AS (
         CASE 
             WHEN (wins + losses) > 0 THEN (wins / (wins + losses)) * 100 
             ELSE 0 
         END
     ) STORED,
     user_id BIGINT NOT NULL,
     dateCreated DATE NOT NULL,
     dateUpdate DATE NOT NULL,
     FOREIGN KEY (user_id) REFERENCES xcentral.users(id) ON DELETE CASCADE
 );
