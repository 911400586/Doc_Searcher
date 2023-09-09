
drop database if exists blog_system;

create database blog_system DEFAULT CHARACTER SET utf8mb4;



use blog_system;


drop table if exists userinfo;

create table userinfo(
                         id int primary key auto_increment,
                         username varchar(100) not null,
                         password varchar(128) not null,
                         photo varchar(500) default '',
                         createtime timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         updatetime timestamp DEFAULT '2023-07-03',
                         `state` int default 1
) default charset 'utf8mb4';


drop table if exists articleinfo;

create table articleinfo(
                            id int primary key auto_increment,
                            title varchar(100) not null,
                            content text not null,
                            createtime timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            updatetime timestamp DEFAULT '2023-07-03',
                            uid int not null,
                            rcount int not null default 1,
                            `state` int default 1

)default charset 'utf8mb4';
