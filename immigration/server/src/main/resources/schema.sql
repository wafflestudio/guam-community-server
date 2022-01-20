create TABLE users
(
    id bigint auto_increment primary key,
    firebase_id varchar(100) not null,
    device_id varchar(100) default null
);
