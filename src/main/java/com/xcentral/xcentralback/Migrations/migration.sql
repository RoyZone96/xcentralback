-- Migration to add columns for id and name in blades table
-- Migration to create blades table
CREATE TABLE blades_parts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    blade_name VARCHAR(255)
);

-- Migration to create ratchets table
CREATE TABLE ratchet (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ratchet VARCHAR(255)
);

-- Migration to create bits table
CREATE TABLE bittype (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bit VARCHAR(255)
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
);


CREATE TABLE submissions (
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
