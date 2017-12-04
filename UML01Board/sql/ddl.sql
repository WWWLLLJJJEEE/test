DROP TABLE user_info_type;
DROP TABLE user_type;
DROP SEQUENCE user_type_seq;
DROP TABLE board;
DROP SEQUENCE board_seq;
DROP TABLE user_info;

CREATE TABLE user_info (
	email VARCHAR2(320) NOT NULL,
	password VARCHAR2(60) NOT NULL,
	name VARCHAR2(20) NOT NULL,
	avatar VARCHAR2(255),
	PRIMARY KEY (email)
);

CREATE TABLE user_type (
	id NUMBER NOT NULL,
	type VARCHAR2(30) NOT NULL,
	PRIMARY KEY (id)
);

CREATE SEQUENCE user_type_seq
START WITH 1 INCREMENT BY 1;

CREATE TABLE user_info_type (
	user_info_email VARCHAR2(320) NOT NULL,
	user_type_id NUMBER NOT NULL,
	PRIMARY KEY (user_info_email, user_type_id),
	CONSTRAINT fk_user_info FOREIGN KEY (user_info_email) REFERENCES user_info (email),
	CONSTRAINT fk_user_type FOREIGN KEY (user_type_id) REFERENCES user_type (id)
);

CREATE TABLE board (
   no 			NUMBER PRIMARY KEY,
   title  		VARCHAR2(100),
   content  	VARCHAR2(4000),
   id			VARCHAR2(100),
   regdate  	DATE,
   attachment	VARCHAR2(255),
   CONSTRAINT fk_board FOREIGN KEY (id) REFERENCES user_info(email)
);

CREATE SEQUENCE board_seq
START WITH 1 INCREMENT BY 1;