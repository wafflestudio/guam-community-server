delete from boards;
delete from post_categories;
delete from post_comments;
delete from categories;
delete from posts;

insert into boards (id, title) values (1, 'anonymous'), (2, 'study');
insert into categories (id, title) values (1, 'Programming');

insert into posts (id, board_id, user_id, title, content, images, status, created_at, updated_at) values
  (1, 3, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00'),
  (2, 1, 1, 'hi2keyword', 'bye2', '/post/2/1.png,/post/2/2.png', 'VALID', '2021-10-10 09:01:00', '2021-10-10 09:01:00'),
  (3, 1, 2, 'hi2', 'bye2keyword', '/post/2/1.png,/post/2/2.png', 'VALID', '2021-10-10 09:01:00', '2021-10-10 09:01:00');

insert into post_categories (post_id, category_id) values (1, 1), (2, 1);

insert into post_comments (id, post_id, user_id, content, images, created_at, updated_at) values
  (1, 1, 2, 'wassup', '/comment/1/1.png,/comment/1/2.png', '2021-10-10 09:10:00', '2021-10-10 09:10:00'),
  (2, 2, 1, 'sssup', null, '2021-10-10 09:09:00', '2021-10-10 09:12:00'),
  (3, 2, 2, 'sssup', null, '2021-10-10 09:09:00', '2021-10-10 09:12:00'),
  (4, 2, 3, 'sssup', null, '2021-10-10 09:09:00', '2021-10-10 09:12:00');