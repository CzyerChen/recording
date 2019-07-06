### 数字类型
- TinyInt[M] [UNSIGNED]	-128~127  UNSIGNED ： 0~255
- SmallInt[M] [UNSIGNED]	-32768~32767 UNSIGNED ：0~ 65535
- MediumInt[M] [UNSIGNED]	-8388608~8388607  UNSIGNED ：0~16777215
- Int[M] [UNSIGNED]	-2^31~2^31-1 UNSIGNED ： 0~2^32
- BigInt[M] [UNSIGNED]	-2^63~2^63-1 UNSIGNED ： 0~2^64
- Float [(M,D)]	-3.4E+38~3.4E+38( 约 )
- Double [(M,D)]	-1.79E+308~1.79E+308( 约 )
- Decimal [(M,D)]

### 时间类型
- Date	日期(yyyy-mm-dd)
- Time	时间(hh:mm:ss)
- DateTime	日期与时间組合(yyyy-mm-dd hh:mm:ss)
- TimeStamp	yyyymmddhhmmss

### 文本类型
- TinyText	最大长度255个字节(2^8-1)

-  Text	最大长度65535个字节(2^16-1)

- MediumText	最大长度 16777215 个字节(2^24-1)

- LongText	最大长度4294967295个字节 (2^32-1)

