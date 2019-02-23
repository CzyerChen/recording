- 创建用户
```text
mysql> insert into mysql.user(Host,User,Password) values("localhost","admin",password("123456"))
```
- 授权
```text
grant all privileges on test.* to admin@localhost identified by '123456';

# 授予全部权限
grant all privileges on test.* to admin@localhost;
grant all on test.* to admin@localhost;
grant all on test.stu_profile to admin@localhost;

#选择，插入，更新
grant select, insert, update, delete on testdb.* to common@'%';

#创建 修改 删除
grant create on testdb.* to developer@'192.168.0.%';
grant alter  on testdb.* to developer@'192.168.0.%';
grant drop   on testdb.* to developer@'192.168.0.%';

#外键权限
grant references on testdb.* to developer@'192.168.0.%';

#视图
grant create view on testdb.* to developer@'192.168.0.%';
grant show   view on testdb.* to developer@'192.168.0.%';

#存储过程，权限
grant create routine on testdb.* to developer@'192.168.0.%'; -- now, can show procedure status
grant alter  routine on testdb.* to developer@'192.168.0.%'; -- now, you can drop a procedure
grant execute        on testdb.* to developer@'192.168.0.%';

```
完成授权后一定要刷新权限
```text
mysql>flush privileges;
```
mysql授权表共有5个表：user、db、host、tables_priv和columns_priv,可以查看具体信息


- 删除用户
```text
mysql>DELETE FROM user WHERE User="admin" and Host="localhost";
```

- 修改用户
```text
mysql>update mysql.user set password=password('111111') where User="admin" and Host="localhost";
```
刷新权限表
```text
mysql>flush privileges;
```

- 查看权限
```text
show grants;
```

- 撤销权限
```text
revoke all on *.* from admin@localhost;
```
