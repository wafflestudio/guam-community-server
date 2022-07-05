delete from boards;
delete from post_categories;
delete from post_comments;
delete from categories;
delete from posts;

insert into boards (id, title) values (1, 'chat'), (2, 'study');

insert into posts (id, board_id, user_id, title, content, images, status, created_at, updated_at) values
    (1, 2, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00'),
    (2, 1, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00');

insert into post_comments (id, post_id, user_id, mentioned_user_ids, status, content, images, created_at, updated_at) values
    (1, 1, 1, '', 'VALID', 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:00', '2021-10-10 09:10:00'),
    (2, 1, 2, '', 'VALID', 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:01', '2021-10-10 09:10:00'),
    (6, 1, 2, '', 'DELETED', 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:02', '2021-10-10 09:10:00'),
    (3, 2, 1, '', 'VALID', 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:03', '2021-10-10 09:10:00'),
    (4, 2, 2, '', 'VALID', 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:04', '2021-10-10 09:10:00'),
    (5, 2, 3, '', 'VALID', 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:05', '2021-10-10 09:10:00');