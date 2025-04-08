-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)      null,
    userAccount  varchar(256)      null,
    avatarUrl    varchar(1024)     null,
    gender       tinyint           null,
    userPassword varchar(512)      not null,
    phone        varchar(128)      null,
    email        varchar(512)      null,
    userStatus   int     default 0 not null comment '0 - normal',
    createTime   datetime          null,
    updateTime   datetime          null,
    isDelete     tinyint default 0 not null,
    userRole     int     default 0 null,
    planetCode   varchar(512)      null
);