insert into boards (id, title) values (1, 'chat'), (2, 'study');
insert into users (id, immigration_id, nickname) values (1, 1, 'jon'), (2, 2, 'snow'), (3, 3, 'tony'), (4, 4, 'whitty');
insert into tags (id, title) values (1, 'Programming'), (2, 'Data Science');

insert into posts (id, board_id, user_id, title, content, images, status, created_at, updated_at) values
(1, 1, 1, 'hi', 'bye', null, 'VALID', '2021-10-10 09:00:00', '2021-10-10 09:00:00');

insert into post_tags (post_id, tag_id) values (1, 1);