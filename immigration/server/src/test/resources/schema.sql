create TABLE users
(
    id bigint auto_increment primary key,
    firebase_user_id varchar(100) not null,
    firebase_device_id varchar(100) default null
);
