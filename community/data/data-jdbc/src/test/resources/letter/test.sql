insert into letters(user_id, sent_by, sent_to, text, status, is_read) values
(1, 1, 2, '안녕하세요', 'ACTIVE', false),
(1, 2, 1, '네 안녕하세요', 'ACTIVE', false),
(1, 1, 2, '괌 개발 어떠신가요', 'ACTIVE', false),
(1, 2, 1, '할만하네요', 'ACTIVE', false),
(1, 1, 2, '전 아닌데요', 'DELETED', false),
(1, 1, 3, '안녕하세요 2', 'ACTIVE', false),
(1, 3, 1, '네 안녕하세요 2', 'ACTIVE', false),
(1, 1, 3, '괌 클라이언트 어떠신가요', 'ACTIVE', false),
(1, 3, 1, '재밌어요', 'ACTIVE', false);