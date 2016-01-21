DROP TABLE photo;
DROP TABLE user;
CREATE TABLE IF NOT EXISTS user (
	user_name CHAR(20) NOT NULL UNIQUE check(user_name != ''),
	user_password CHAR(20) NOT NULL,
	user_email CHAR(40) NOT NULL UNIQUE,
	
	PRIMARY KEY(user_name)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS photo (
	photo_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	photo_path VARCHAR(100) NOT NULL,
	user_name CHAR(20) NOT NULL UNIQUE,
	
	FOREIGN KEY(user_name) REFERENCES user(user_name) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=innodb DEFAULT CHARSET=utf8; 
	