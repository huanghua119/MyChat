create database myqq  CHARACTER SET  utf8  COLLATE utf8_general_ci；

use myqq;

create table User
(
   userId                  varchar(50) primary key,
   userName                varchar(100),
   userPass                varchar(100),
   userSex                 varchar(10),
   userAddress             varchar(100),
   userAge                 int
);

create table NumberPool
(
    numberstart            int,
    numberend              int
);
insert into NumberPool values(10000,100000);