delete from boards;
delete from post_categories;
delete from post_comments;
delete from categories;
delete from posts;

insert into boards (id, title) values (1, 'chat'), (2, 'study');

insert into posts (id, board_id, user_id, title, content, images, status, created_at, updated_at) values
    (1, 1, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00');

insert into post_comments (id, post_id, user_id, content, images, created_at, updated_at) values
(1, 1, 1, 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:00', '2021-10-10 09:10:00');