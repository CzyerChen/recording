- mysql 存储emoji是一个要构建聊天系统肯定会面临的需求，这也很常见
- 这个表情包文件可以下载到，关键就是看怎么存储
- 现在市场一般用的版本都是mysql的5.x版本，默认编码utf8,不像新的mysql 8 默认编码是utf8mb4
- 提到编码就是因为这个emoji的存储和这个编码息息相关
- emoji表情是1个字符占4个字节，而utf8编码只支持1-3个字节的字符，所以utf8编码下，如果直接存储表情，数据库存储会失败

### 修改编码
- 需要修改的地方有
```text
1.库(database)的编码
2.表(table)的编码
3.字段(column)的编码
4.程序中，数据库的连接url
5.mysql的配置文件my.conf中
```
- 有人问，这样修改了编码对别的数据存储有影响吗？可以看因版本都之间修改了默认编码，你觉得这样会有影响吗？应该是不会的啦
- 通过SQL查看编码：
```text
SHOW VARIABLES LIKE 'character_set_%'
SHOW VARIABLES LIKE 'collation_%'

setcharacter_set_client=utf8mb4;
set character_set_connection=utf8mb4;
set character_set_database=utf8mb4;
set character_set_results=utf8mb4;
set character_set_server=utf8mb4;
```
- 修改库编码：
```text
#建库的时候指定编码
datebase charset=utf8mb4 
datebase collation=utf8mb4_unicode_ci
```
- 修改表编码：
```text
#建表的时候指定编码
character set=utf8mb4
collation=utf8mb4_unicode_ci
```
- 修改my.conf
```text
[client]
default-character-set = utf8mb4
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
[mysql]
default-character-set = utf8mb4
```
- my.conf是数据库的配置文件，修改了就一定要重启数据库哦
```text
/etc/init.d/mysql restart
```
- 最后记得应用程序里面，datasource.url的书写将characterEncoding修改为utf8mb4，不然出来的符号全是？？？
- 最后就自己测试一下把


### 表情数据
```text
 表的结构 `biaoqing`


CREATE TABLE IF NOT EXISTS `biaoqing` (
  `id` int(5) DEFAULT NULL COMMENT '表情id',
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT ' 表情名',
  `des` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '描述',
  `url` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '表情路径'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 ;

 转存表中的数据 `biaoqing`

INSERT INTO `weixin_biaoqing` (`id`, `name`, `des`, `url`) VALUES
(1, '微笑', '/::)', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/0.gif'),
(2, '撇嘴', '/::~', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/1.gif'),
(3, '色', '/::B', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/2.gif'),
(4, '发呆', '/::|', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/3.gif'),
(5, '得意', '/:8-)', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/4.gif'),
(6, '流泪', '/::<', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/5.gif'),
(7, '害羞', '/::$', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/6.gif'),
(8, '闭嘴', '/::X', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/7.gif'),
(9, '睡', '/::Z', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/8.gif'),
(10, '尴尬', '/::-|', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/9.gif'),
(11, '发怒', '/::@', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/10.gif'),
(12, '调皮', '/::P', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/11.gif'),
(13, '呲牙', '/::D', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/12.gif'),
(14, '惊讶', '/::O', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/13.gif'),
(15, '难过', '/::(', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/14.gif'),
(16, '酷', '/::+', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/15.gif'),
(17, '冷汗', '/:--b', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/16.gif'),
(18, '抓狂', '/::Q', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/17.gif'),
(19, '吐', '/::T', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/18.gif'),
(20, '偷笑', '/:,@P', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/19.gif'),
(21, '可爱', '/:,@-D', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/20.gif'),
(22, '白眼', '/::d', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/21.gif'),
(23, '傲慢', '/:,@o', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/22.gif'),
(24, '饥饿', '/::g', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/23.gif'),
(25, '困', '/:|-)', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/24.gif'),
(26, '惊恐', '/::!', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/25.gif'),
(27, '流汗', '/::L', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/26.gif'),
(28, '憨笑', '/::>', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/27.gif'),
(29, '大兵', '/::,@', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/28.gif'),
(30, '努力', '/:,@f', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/29.gif'),
(31, '咒骂', '/::-S', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/30.gif'),
(32, '疑问', '/:?', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/31.gif'),
(33, '嘘', '/:,@x', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/32.gif'),
(34, '晕', '/:,@@', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/33.gif'),
(35, '折磨', '/::8', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/34.gif'),
(36, '衰', '/:,@!', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/35.gif'),
(37, '骷髅', '/:!!!', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/36.gif'),
(38, '敲打', '/:xx', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/37.gif'),
(39, '再见', '/:bye', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/38.gif'),
(40, '擦汗', '/:wipe', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/39.gif'),
(41, '抠鼻', '/:dig', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/40.gif'),
(42, '鼓掌', '/:handclap', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/41.gif'),
(43, '溴大了', '/:&-(', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/42.gif'),
(44, '坏笑', '/:B-)', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/43.gif'),
(45, '左哼哼', '/:<@', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/44.gif'),
(46, '右哼哼', '/:@>', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/45.gif'),
(47, '哈欠', '/::-O', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/46.gif'),
(48, '鄙视', '/:>-|', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/47.gif'),
(49, '委屈', '/:P-(', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/48.gif'),
(50, '阴险', '/:X-)', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/49.gif'),
(51, '亲亲', '/::*', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/50.gif'),
(52, '吓', '/:@x', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/51.gif'),
(53, '可怜', '/:8*', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/52.gif'),
(54, '菜刀', '/:pd', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/53.gif'),
(55, '西瓜', '/:<W>', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/54.gif'),
(56, '啤酒', '/:beer', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/55.gif'),
(57, '篮球', '/:basketb', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/56.gif'),
(58, '乒乓', '/:oo', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/57.gif'),
(59, '咖啡', '/:coffee', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/58.gif'),
(60, '饭', '/:eat', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/59.gif'),
(61, '猪头', '/:pig', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/60.gif'),
(62, '玫瑰', '/:rose', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/61.gif'),
(63, '凋谢', '/:fade', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/62.gif'),
(64, '示爱', '/:showlove', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/63.gif'),
(65, '爱心', '/:heart', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/64.gif'),
(66, '心碎', '/:break', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/65.gif'),
(67, '蛋糕', '/:cake', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/66.gif'),
(68, '闪电', '/:li', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/67.gif'),
(69, '炸弹', '/:bome', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/68.gif'),
(70, '刀', '/:kn', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/69.gif'),
(71, '瓢虫', '/:ladybug', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/70.gif'),
(72, '便便', '/:shit', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/71.gif'),
(73, '月亮', '/:moon', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/72.gif'),
(74, '太阳', '/:sun', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/73.gif'),
(75, '礼物', '/:gift', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/74.gif'),
(76, '拥抱', '/:hug', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/75.gif'),
(77, '强', '/:strong', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/76.gif'),
(78, '弱', '/:weak', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/77.gif'),
(79, '握手', '/:share', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/78.gif'),
(80, '胜利', '/:v', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/79.gif'),
(81, '抱拳', '/:@)', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/80.gif'),
(82, '勾引', '/:jj', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/81.gif'),
(83, '拳头', '/:@@', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/82.gif'),
(84, '差劲', '/:bad', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/83.gif'),
(85, '爱你', '/:lvu', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/84.gif'),
(86, 'no', '/:no', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/85.gif'),
(87, 'ok', '/:ok', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/86.gif'),
(88, '爱情', '/:love', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/87.gif'),
(89, '飞吻', '/:<L>', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/88.gif'),
(90, '跳舞', '/:jump', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/89.gif'),
(91, '发抖', '/:shake', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/90.gif'),
(92, '怄火', '/:<O>', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/91.gif'),
(93, '转圈', '/:circle', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/92.gif'),
(94, '磕头', '/:kotow', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/93.gif'),
(95, '回头', '/:turn', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/94.gif'),
(96, '跳绳', '/:skip', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/95.gif'),
(97, '挥手', '/:oY', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/96.gif'),
(98, '激动', '/:#-0', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/97.gif'),
(99, '街舞', '/:hiphot', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/98.gif'),
(100, '献吻', '/:kiss', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/99.gif'),
(101, '左太极', '/:<&', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/100.gif'),
(102, '右太极', '/:&>', 'http://res.mail.qq.com/zh_CN/images/mo/DEFAULT2/101.gif');

```
### 别人的[emoji操作](https://www.jianshu.com/p/b33a04bd04d6)
### emoji [unicode对应大全](https://blog.csdn.net/Draling/article/details/52104649)
