-- 创建数据库
drop database if exists blog_system;
create database blog_system DEFAULT CHARACTER SET utf8mb4;
-- 使⽤数据数据
use blog_system;
-- 创建表[⽤户表]
drop table if exists userinfo;
create table userinfo(
                         id int primary key auto_increment,
                         username varchar(100) not null,
                         password varchar(32) not null,
                         photo varchar(500) default '',
                         createtime datetime default current_timestamp(),
                         updatetime datetime default current_timestamp(),
                         `state` int default 1
) default charset 'utf8mb4';
-- 创建⽂章表
drop table if exists articleinfo;
create table articleinfo(
                            id int primary key auto_increment,
                            title varchar(100) not null,
                            content text not null,
                            createtime datetime default current_timestamp(),
                            updatetime datetime default current_timestamp(),
                            uid int not null,
                            rcount int not null default 1,
                            `state` int default 1
)default charset 'utf8mb4';
