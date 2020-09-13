CREATE TABLE IF NOT EXISTS `caller_group` (
    `name` LONGTEXT,
    `prefix` LONGTEXT,
    `description` TEXT,
);

CREATE TABLE IF NOT EXISTS `call_format` (
    `name` LONGTEXT,
    `format` TEXT,
    `description` TEXT,
);

CREATE TABLE IF NOT EXISTS `caller` (
    `user_id` LONGTEXT,
    `caller_group_id` LONGTEXT,
    `order_number` INT,
    `enabled` BOOLEAN,
);

CREATE TABLE IF NOT EXISTS `ticket_directory` (
    `name` LONGTEXT,
    `key_code` LONGTEXT,
    `caller_group_id` LONGTEXT,
    `prefix` LONGTEXT,
    `prefer_caller_group_prefix` BOOLEAN,
    `description` TEXT,
    `call_format_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `subsystem` (
    `name` LONGTEXT,
    `ip_address` LONGTEXT,
    `subsystem_type` LONGTEXT,
);

CREATE TABLE IF NOT EXISTS `call_director` (
    `caller_id` BIGINT,
    `subsystem_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `announcer` (
    `caller_id` BIGINT,
    `caller_group_id` BIGINT,
    `prefer_caller_group` BOOLEAN,
    `subsystem_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `displayer` (
    `caller_id` BIGINT,
    `caller_group_id` BIGINT,
    `prefer_caller_group` BOOLEAN,
    `subsystem_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `av_announcer` (
    `caller_id` BIGINT,
    `caller_group_id` BIGINT,
    `prefer_caller_group` BOOLEAN,
    `subsystem_id` BIGINT,
);

CREATE TABLE IF NOT EXISTS `quality_of_service_log` (
    `queue_id` BIGINT,
    `calls_span_time` INT,
    `recall_count` INT,
);

CREATE TABLE IF NOT EXISTS `ticket` (
    `entry_number` BIGINT,
    `ticket_directory_id` BIGINT,
    `caller_id` BIGINT,
    `entry_date_epoch` BIGINT,
    `entry_date` LONGTEXT,
    `registered_time_epoch` BIGINT,
    `registered_time` LONGTEXT,
    `called_time_epoch` BIGINT,
    `called_time` LONGTEXT,
    `queued_time_epoch` BIGINT,
    `queued_time` LONGTEXT,
    `printed` BOOLEAN,
    `cancelled` BOOLEAN,
    `called` BOOLEAN,
    `queued` BOOLEAN,
);

CREATE TABLE IF NOT EXISTS `queue` (
    `ticket_id` BIGINT,
    `caller_id` BIGINT,
    `called_time_epoch` BIGINT,
    `called_time` LONGTEXT,
    `queued_date_epoch` BIGINT,
    `queued_date` LONGTEXT,
    `queued_time_epoch` BIGINT,
    `queued_time` LONGTEXT,
    `queued` BOOLEAN,
);
