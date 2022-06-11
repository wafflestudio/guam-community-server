insert into `letters`(letter_box_id, is_read, sent_by, sent_to, text, user_id) values
(1, false, 1, 2, '1 to 2', 1),
(1, false, 2, 1, '2 to 1', 2),
(1, true, 1, 2, '1 to 2(2)', 1),
(3, false, 1, 3, '1 to 3', 1),
(2, false, 2, 3, '2 to 3', 2),
(2, false, 2, 3, '2 to 3(2)', 2);

insert into `letter_boxes`(low_id, high_id) values
(1, 2),
(2, 3),
(1, 3);
