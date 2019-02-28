### 关于 MySQL 的 boolean 和 tinyint(1)
- MYSQL保存BOOLEAN值时用1代表TRUE,0代表FALSE
- boolean在MySQL里的类型为tinyint(1),
- MySQL里有四个常量：true,false,TRUE,FALSE,它们分别代表1,0,1,0
- mysql中不存在Boolean的类型，在pojo的Boolean类型写入数据库的时候会自动转换为tinyint(1)类型，也就是说mysql把boolean=tinyInt了

