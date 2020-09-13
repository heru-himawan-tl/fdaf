CREATE TABLE IF NOT EXISTS `user` (
    `user_name` LONGTEXT,
    `password` LONGTEXT,
    `email` LONGTEXT,
    `time_stamp` BIGINT,
    `employee_id` BIGINT,
    `user_type` LONGTEXT,
    `enabled` BOOLEAN,
);

CREATE TABLE IF NOT EXISTS `dummy` (
);

CREATE TABLE IF NOT EXISTS `employee` (
    `first_name` LONGTEXT,
    `middle_name` LONGTEXT,
    `last_name` LONGTEXT,
    `dob_day` INT,
    `dob_month` INT,
    `dob_year` INT,
    `ssn` LONGTEXT,
    `gender` LONGTEXT,
    `employment_id` LONGTEXT,
    `department_id` BIGINT,
    `role_id` BIGINT,
    `address1` TEXT,
    `address2` TEXT,
    `phone1` LONGTEXT,
    `phone2` LONGTEXT,
);

CREATE TABLE IF NOT EXISTS `department` (
    `name` LONGTEXT,
    `description` TEXT,
);

CREATE TABLE IF NOT EXISTS `role` (
    `name` LONGTEXT,
    `description` TEXT,
);

CREATE TABLE IF NOT EXISTS `user_group` (
    `name` LONGTEXT,
    `description` TEXT,
);

CREATE TABLE IF NOT EXISTS `user_group_member` (
    `user_id` BIGINT,
    `user_group_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `user_group_program_assoc` (
    `program_name` LONGTEXT,
    `user_group_id` BIGINT,
    `allow_write` BOOLEAN,
    `allow_delete` BOOLEAN,
    `allow_change_permission` TINYINT,
);

CREATE TABLE IF NOT EXISTS `staff_invitation` (
    `email` LONGTEXT,
    `signature` LONGTEXT,
    `subject` LONGTEXT,
    `message` TEXT,
);

CREATE TABLE IF NOT EXISTS `user_login` (
    `user_id` BIGINT,
    `in_epoch_time_stamp` BIGINT,
    `in_time_stamp` LONGTEXT,
    `live_time` BIGINT,
    `user_session_id` LONGTEXT,
    `logout_flag` TINYINT,
    `logout_state` BOOLEAN,
    `out_epoch_time_stamp` BIGINT,
    `out_time_stamp` LONGTEXT,
    `user_agent` LONGTEXT,
);

CREATE TABLE IF NOT EXISTS `author` (
    `user_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `modifier` (
    `user_id` BIGINT,
);
