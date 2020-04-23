CREATE TABLE `command_queue` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`executed` TINYINT(1) NOT NULL DEFAULT 0,
	`command` VARCHAR(255) NOT NULL,
	`arguments` VARCHAR(1024) NOT NULL,
	`time` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	PRIMARY KEY (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;
