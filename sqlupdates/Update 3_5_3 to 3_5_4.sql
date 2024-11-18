--New bot walking settings
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.limit.walking.distance', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.limit.walking.distance.radius', '5');

--New permission
ALTER TABLE `permissions` ADD COLUMN `acc_unignorable` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `acc_infinite_friends`;