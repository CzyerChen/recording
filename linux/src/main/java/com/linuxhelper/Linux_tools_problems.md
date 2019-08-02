####  压缩、解压缩：yum install zip unzip （zip  unzip）

#### 上传下载：sudo yum install lrzsz -y （rz sz）

#### 解决Ssh/Scp报错：Someone Could Be Eavesdropping On You Right Now (Man-In-The-Middle Attack)!
以上在进行ssh scp sftp的时候都可能出现
```text
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  
@    WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!     @  
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  
IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!  
Someone could be eavesdropping on you right now (man-in-the-middle attack)!  
It is also possible that a host key has just been changed.  
The fingerprint for the ECDSA key sent by the remote host is  
df:33:37:b6:7b:c9:e5:19:65:f7:38:ad:94:b2:9e:36.  
Please contact your system administrator.  
Add correct host key in /root/.ssh/known_hosts to get rid of this message.  
Offending ECDSA key in /root/.ssh/known_hosts:1  
ECDSA host key for 192.168.1.*** has changed and you have requested strict checking.  
Host key verification failed.  
lost connection 
```
- 从报错信息看是因为目标主机key【比如重做系统或者还原】与已保存的key不同导致认证失败！
- key算法为ECDSA，百度可知为椭圆曲线数字签名算法
- 解决方法很容易：就是删除写入的连接信息 
````text
如果是root用户给：
rm -f  /root/.ssh/known_hosts

如果是其他用户，就在对应用户的根目录下适应
rm -f  /home/xxx/.ssh/known_hosts  
````  


#### Linux sftp 上传下载文件 permission denied
- 这边atmoz/sftp,fauria/vsftpd 两种docker镜像都用过，最后是选择了使用人数较多的前者；
- [拍错思路参考](https://blog.csdn.net/guoxiaoniu/article/details/40786419)
```text
1、查看要上传的文件权限，发现我新建的testfile 文件权限为-rw-r--r--，应该不是这个文件权限的问题，不过为了排除，还是修改了权限，改成所有权限都有，chmod 777 testfile
到这里真的有可能能够解决问题，主要是跟文件的用户组权限，以及sftp的一些细节相关，授予了777权限，那会解决很多权限问题


2、修改权限之后，重新修改，发现还是denied，上网查，有同学指出修改配置文件中的参数，在/etc/vsftpd.conf配置文件最后增加一行即可：write_enable=YES，检查/etc/vsftpd.conf配置文件中的write_enable，发现它的默认值是YES，排除这个原因

3、接下来考虑是不是pub文件夹权限问题，ftp>ls 查看pub文件夹详细信息（要先ftp>cd ..返回上一级目录），发现pub文件夹具有所有的权限，排除这个因素

4、设置/etc/vsftpd.conf配置文件中的anonymous_enable=YES，anon_upload_enable=YES,anon_mkdir_write_enable=YES，修改完毕后，重启ftp服务，重新连接

5、上传成功后，使用get命令下载一个文件到本地，ftp>get testput testget（注意，此时testget这个文件会下载到本地当前所在目录，比如说我是在/tmp目录下执行的ftp localhost，此时下载的testget文件会存放在/tmp目录下），报错：failed to open file

排错思路：

1、第一下想到的是文件权限问题，ftp>ls查看后，发现testput文件为只读权限，于是要修改，结果直接在ftp命令行模式下输入ftp>chmod 777 testput，提示permission denied，ftp中是不可以修改文件及文件夹权限的

2、修改步骤：cd /var/ftp/pub ；chmod 777 testput

3、重新执行ftp>get testput testget，即可下载，进入相应的目录可以查看到下载下来的文件

```
