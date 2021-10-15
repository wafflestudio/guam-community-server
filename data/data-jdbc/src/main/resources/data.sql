insert into boards (title) values ('chat'), ('study');
insert into users (username) values ('jon'), ('snow'), ('tony'), ('whitty');
insert into tags (title) values ('Programming'), ('Data Science');

insert into posts (board_id, user_id, title, content, images, status, created_at, updated_at) values
(1, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00'),
(1, 1, 'hi2', 'bye2', '/post/2/1.png,/post/2/2.png', 'VALID', '2021-10-10 09:01:00', '2021-10-10 09:01:00'),
(1, 2, 'hi', 'bye', null, 'VALID', '2021-10-10 09:02:00', '2021-10-10 09:02:00'),
(2, 2, 'study', 'hard', null, 'VALID', '2021-10-10 09:03:00', '2021-10-10 09:03:00');

insert into post_tags (post_id, tag_id) values (1, 1), (1, 2), (2, 1), (3, 1), (4, 2);
insert into post_likes (post_id, user_id) values (1, 1), (1, 3), (1, 4), (2, 2), (2, 3), (2, 4)
