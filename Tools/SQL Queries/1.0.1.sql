ALTER TABLE `players`
	CHANGE COLUMN `auth_ticket` `auth_ticket` VARCHAR(100) NULL;

DROP TABLE `navigator_featured_rooms`;

CREATE TABLE IF NOT EXISTS `navigator_publics` (
  `room_id` int(11) NOT NULL AUTO_INCREMENT,
  `caption` varchar(64) NOT NULL,
  `description` varchar(150) NOT NULL,
  `image_url` text NOT NULL,
  `order_num` int(11) NOT NULL DEFAULT '1',
  `enabled` enum('0','1') NOT NULL DEFAULT '1',
  PRIMARY KEY (`room_id`),
  KEY `ordernum` (`order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `navigator_staff_picks` (
  `room_id` int(11) NOT NULL DEFAULT '0',
  `featured_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `navigator_categories`
(`id`, `category`, `category_identifier`, `public_name`, `category_type`, `search_allowance`, `order_id`)
	VALUES (38, 'official_view', 'official-root', 'Staff Picks', 'staff_picks', 'SHOW_MORE', 2);

UPDATE navigator_categories SET category_type = 'public' WHERE category_type = 'featured';

CREATE TABLE `items_wired_rewards` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`player_id` INT(11) NOT NULL,
	`item_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `item_id` (`item_id`)
) COLLATE='latin1_swedish_ci' ENGINE=InnoDB;

ALTER TABLE `rooms`
	ADD COLUMN `group_id` INT(11) NOT NULL DEFAULT '0' AFTER `owner_id`;

UPDATE rooms r SET r.group_id = IFNULL((SELECT id FROM groups WHERE room_id = r.id LIMIT 1), 0);

CREATE TABLE `pet_messages` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`pet_type` INT(11) NOT NULL DEFAULT '0',
	`message_type` ENUM('GENERIC','SCRATCHED','WELCOME_HOME','HUNGRY','TIRED') NOT NULL DEFAULT 'GENERIC',
	`message_string` VARCHAR(100) NOT NULL DEFAULT 'Hiya %username%!!!',
	PRIMARY KEY (`id`)
) COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `pet_transformable` (
  `name` varchar(50) NOT NULL,
  `data` varchar(50) NOT NULL COMMENT 'The first number is the pet ID, the rest is what determine the colours, hair etc.',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `pet_transformable` (`name`, `data`) VALUES
	('bear', '4 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('bunny', '17 0 FFFFFF 0 0 0 0 0 0 0#0'),
	('cat', '1 0 F5E759 2 2 -1 0 3 -1 0#0'),
	('chick', '10 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('crocodile', '2 4 96E75A 2 2 -1 0 3 -1 0#4'),
	('dog', '0 15 FEE4B2 2 2 -1 0 3 -1 0#15'),
	('dragon', '12 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('frog', '11 12 FFFFFF 2 2 -1 0 3 -1 0#12'),
	('horse', '15 3 FFFFFF 2 2 -1 0 3 -1 0#3'),
	('human', ''),
	('lion', '6 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('monkey', '14 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('monster_plant', '16 0 FFFFFF 0 0 0 0 0 0 0#0'),
	('pig', '5 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('rhino', '7 0 CCCCCC 2 2 -1 0 3 -1 0#0'),
	('spider', '8 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('terrier', '3 0 FFFFFF 2 2 -1 0 3 -1 0#0'),
	('turtle', '9 0 FFFFFF 2 2 -1 0 3 -1 0#0');

CREATE TABLE IF NOT EXISTS `polls` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL DEFAULT 'Untitled Poll',
  `thanks_message` varchar(100) NOT NULL DEFAULT 'Thanks!',
  `badge_reward` varchar(100) NOT NULL DEFAULT 'US8',
  `room_id` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `polls_answers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `poll_id` int(11) DEFAULT '0',
  `question_id` int(11) DEFAULT '0',
  `player_id` int(11) DEFAULT '0',
  `answer` varchar(1000) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `poll_id_question_id_player_id` (`poll_id`,`question_id`,`player_id`),
  KEY `player_id` (`player_id`),
  KEY `poll_id` (`poll_id`),
  KEY `poll_id_question_id` (`poll_id`,`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `polls_questions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `poll_id` int(11) NOT NULL,
  `question_type` enum('WORDED','MULTIPLE_CHOICE') NOT NULL DEFAULT 'WORDED',
  `question` varchar(100) NOT NULL DEFAULT 'What do you think of Comet Server?',
  `options` text,
  PRIMARY KEY (`id`),
  KEY `poll_id` (`poll_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `navigator_categories`
	ADD COLUMN `room_count` INT(11) NULL DEFAULT '12' AFTER `order_id`,
	ADD COLUMN `room_count_expanded` INT(11) NULL DEFAULT '50' AFTER `room_count`,
	ADD COLUMN `visible` ENUM('1','0') NULL DEFAULT '1' AFTER `room_count_expanded`;
