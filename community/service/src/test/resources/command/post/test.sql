delete from boards;
delete from post_categories;
delete from post_comments;
delete from categories;
delete from posts;

insert into boards (id, title) values (1, 'chat'), (2, 'study');
insert into categories (id, title) values (1, 'Programming'), (2, 'Data Science');

insert into posts (id, board_id, user_id, title, content, images, status, created_at, updated_at) values
(1, 1, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00');

insert into post_categories (post_id, category_id) values (1, 1);