DROP TABLE IF EXISTS letters;

DROP TABLE IF EXISTS letter_boxes;

create TABLE `letters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `letter_box_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT now(),
  `updated_at` timestamp NULL DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `is_read` boolean NOT NULL,
  `sent_by` bigint NOT NULL,
  `sent_to` bigint NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `text` varchar(300) DEFAULT NULL,
   PRIMARY KEY (`id`)
);

create TABLE `letter_boxes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `low_id` bigint NOT NULL,
  `high_id` bigint NOT NULL,
  `low_delete_marked_id` bigint DEFAULT NULL,
  `high_delete_marked_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user` (`low_id`,`high_id`)
);