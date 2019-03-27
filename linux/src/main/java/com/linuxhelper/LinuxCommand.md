- 那些年敲过的linux命令，不是运维，也能运维

### ls
用于列出当前目录的文件
```text
ls 
ls -l 
alias ll='ls -l'
```
### ps
用于查看进程并输出，process的意思
```text
-A ：所有的进程均显示出来
-a ：不与terminal有关的所有进程
-u ：有效用户的相关进程
-x ：一般与a参数一起使用，可列出较完整的信息
-l ：较长，较详细地将PID的信息列出
```
```text
ps -ef | grep 'java'
ps aux # 查看系统所有的进程数据
ps ax # 查看不与terminal有关的所有进程
ps -lA # 查看系统所有的进程数据
ps axjf # 查看连同一部分进程树状态
```

### free
能够查看当前系统存储情况
```text
root@node1 ~]# free
             total       used       free     shared    buffers     cached
Mem:      32879196   31738984    1140212      17024     312592   27870352
-/+ buffers/cache:    3556040   29323156
Swap:            0          0          0
```
### top
查看当前系统进程运行情况,包括CPU MEMORY TASK SWAP,Linux下常用的性能分析工具，能够实时显示系统中各个进程的资源占用状况
```text
top - 14:55:39 up 145 days,  4:10,  1 user,  load average: 0.19, 0.25, 0.24
Tasks: 376 total,   1 running, 375 sleeping,   0 stopped,   0 zombie
Cpu(s):  0.6%us,  0.6%sy,  0.0%ni, 98.8%id,  0.0%wa,  0.0%hi,  0.0%si,  0.0%st
Mem:  32879196k total, 31740620k used,  1138576k free,   312592k buffers
Swap:        0k total,        0k used,        0k free, 27870624k cached

  PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND                                                                
 1965 root      20   0 1969m  37m 4720 S 14.6  0.1   2132:48 python2.6                                                               
 2960 ams       20   0  857m  13m 3652 S  2.0  0.0   3442:39 python2.6                                                               
32676 root      20   0 2888m 319m  48m S  1.0  1.0  11:54.35 java                                                                    
  331 root      20   0 2969m 475m  48m S  0.7  1.5  17:26.19 java                                                                    
 5089 root      20   0  9.9g 501m  48m S  0.7  1.6  27:01.68 java                                                                    
 5261 root      20   0 10.0g 667m  42m S  0.7  2.1  37:59.92 java                                                                    
 9529 root      20   0 17368 1580 1016 R  0.3  0.0   0:00.05 top                                                                     
    1 root      20   0 21324 1408 1136 S  0.0  0.0   0:02.26 init                                                                    
    2 root      20   0     0    0    0 S  0.0  0.0   0:00.00 kthreadd                                                                
    3 root      RT   0     0    0    0 S  0.0  0.0   0:02.64 migration/0                                                             
    4 root      20   0     0    0    0 S  0.0  0.0   0:33.44 ksoftirqd/0                                                             
    5 root      RT   0     0    0    0 S  0.0  0.0   0:00.00 stopper/
```
### mv
用于移动文件或者是重命名 move
```text
-f ：force强制的意思，如果目标文件已经存在，不会询问而直接覆盖
-i ：若目标文件已经存在，就会询问是否覆盖
-u ：若目标文件已经存在，且比目标文件新，才会更新
```
```text
mv 源文件 目标文件
```
### rm 
用于删除文件或目录 remove
```text
-f ：就是force的意思，忽略不存在的文件，不会出现警告消息
-i ：互动模式，在删除前会询问用户是否操作
-r ：递归删除，最常用于目录删除，它是一个非常危险的参数
```
```text
rm -rf a.txt
```

### cat 
用于查看文件的内容，可用管道与more和less一起使用
```text
cat file1 从第一个字节开始正向查看文件的内容 
tac file1 从最后一行开始反向查看一个文件的内容 
cat -n file1 标示文件的行数  
```
### find
查找文件
```text
find / -name a.txt 从 '/' 开始进入根文件系统搜索文件和目录 
find / -user aaa 搜索属于用户 'aaa' 的文件和目录 
find /usr/bin -type f -atime +100 搜索在过去100天内未被使用过的执行文件  
whereis java 显示一个二进制文件、源码或man的位置 
which java 显示一个二进制文件或可执行文件的完整路径
find /var/mail/ -size +50M -exec rm {}  删除大于50M的文件：
```
### chmod 
授予权限
```text
ls -l 显示权限 
chmod ugo+rwx directory 设置目录的所有人(u)、群组(g)以及其他人(o)以读（r，4 ）、写(w，2)和执行(x，1)的权限 
chmod go-rwx directory 删除群组(g)与其他人(o)对目录的读写执行权限
```
### chown 
授予所有者
```text
chown user file 改变一个文件的所有人属性 
chown -R user directory 改变一个目录的所有人属性并同时改变改目录下所有文件的属性 
chown user:group file 改变一个文件的所有人和群组属性
```
### grep
用于从上一部分数据中获取新的部分，一般与管道操作结合使用
```text
grep hello a.txt  搜索a.txt中的hello
grep ^hello a.txt  搜索a.txt中hello开头的词
ps -aux | grep java   查找java进程
netstat -nlp | grep '3306'  查找3306端口

```
### jps
用于查看java进程，需要oracle jdk才能使用，openjdk不行
```text
[root@node1 ~]# jps
5089 HMaster
580 SecondaryNameNode
32676 NameNode
10263 Jps
331 DataNode
5261 HRegionServer
```
### kill
停止指定进程
```text
kill -9 38754
```
### netstat
查看端口进程
```text
netstat -tunlp|grep 端口号
```
### routtrace
## du 
### uptime
### ifocnfig
### su/sudo
### dmesg
### iostat
### vmstat
### sar
### htop
### iotop
### smem

ls、ps、free、top、uptime、ifconfig、su/sudo、dmesg、iostat、vmstat、sar、htop、iotop、smem

Smem 是一款命令行下的内存使用情况报告工具
要安装smem这个工具，需要在系统上安装EPEL软件源，安装过程如下：
[root@localhost ~]# yum install epel-release[root@localhost ~]# yum install smem python-matplotlib python-tk


首先，获取当前系统占用CPU最高的前10个进程最简单的方式是通过ps命令组合实现，例如：
[root@localhost ~]# ps aux|head -1;ps aux|sort -rn -k3|head -10

