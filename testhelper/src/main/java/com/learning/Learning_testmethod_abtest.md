### 一、什么ab测试
- 简单轻巧、基于命令行的接口测试工具
- 功能简单，性能较好，最少的机器资源产生最多的访问请求

### 二、安装
- 下载安装包tar:[官网下载源码安装](http://httpd.apache.org/download.cgi#apache24)
- 此处是mac安装：
    - brew install apr
    - brew install pcre
    - brew install apr-util
- 如果是离线安装，就把这三个安装包下载下来，源码安装
```text

wget http://archive.apache.org/dist/apr/apr-1.4.5.tar.gz  
wget http://archive.apache.org/dist/apr/apr-util-1.3.12.tar.gz  
wget http://jaist.dl.sourceforge.net/project/pcre/pcre/8.10/pcre-8.10.zip 

1. apr
tar -zxf apr-1.4.5.tar.gz  
cd  apr-1.4.5
./configure --prefix=/usr/local/apr 
make && make install 


2.apr-util
tar -zxf apr-util-1.3.12.tar.gz  
cd apr-util-1.3.12  
./configure --prefix=/usr/local/apr-util --with-apr=/usr/local/apr/bin/apr-1-config --enable-utf8   
make && make install 

3.pcre
unzip -o pcre-8.10.zip  
cd pcre-8.10  
./configure --prefix=/usr/local/pcre  
make && make install

```
- 安装完成后，注意查看是否已经存在，避免重复安装。
- 安装httpd-2.4.41
- tar -zxvf  httpd-2.4.41.tar
- cd  httpd-2.4.41
- ./configure
- make 
- make install
```text
xxxxx:httpd-2.4.41 xxxxx$ ab --help
ab: wrong number of arguments
Usage: ab [options] [http[s]://]hostname[:port]/path
Options are:
    -n requests     Number of requests to perform
    -c concurrency  Number of multiple requests to make at a time
    -t timelimit    Seconds to max. to spend on benchmarking
                    This implies -n 50000
    -s timeout      Seconds to max. wait for each response
                    Default is 30 seconds
    -b windowsize   Size of TCP send/receive buffer, in bytes
    -B address      Address to bind to when making outgoing connections
    -p postfile     File containing data to POST. Remember also to set -T
    -u putfile      File containing data to PUT. Remember also to set -T
    -T content-type Content-type header to use for POST/PUT data, eg.
                    'application/x-www-form-urlencoded'
                    Default is 'text/plain'
    -v verbosity    How much troubleshooting info to print
    -w              Print out results in HTML tables
    -i              Use HEAD instead of GET
    -x attributes   String to insert as table attributes
    -y attributes   String to insert as tr attributes
    -z attributes   String to insert as td or th attributes
    -C attribute    Add cookie, eg. 'Apache=1234'. (repeatable)
    -H attribute    Add Arbitrary header line, eg. 'Accept-Encoding: gzip'
                    Inserted after all normal header lines. (repeatable)
    -A attribute    Add Basic WWW Authentication, the attributes
                    are a colon separated username and password.
    -P attribute    Add Basic Proxy Authentication, the attributes
                    are a colon separated username and password.
    -X proxy:port   Proxyserver and port number to use
    -V              Print version number and exit
    -k              Use HTTP KeepAlive feature
    -d              Do not show percentiles served table.
    -S              Do not show confidence estimators and warnings.
    -q              Do not show progress when doing more than 150 requests
    -l              Accept variable document length (use this for dynamic pages)
    -g filename     Output collected data to gnuplot format file.
    -e filename     Output CSV file with percentages served
    -r              Don't exit on socket receive errors.
    -m method       Method name
    -h              Display usage information (this message)
    -I              Disable TLS Server Name Indication (SNI) extension
    -Z ciphersuite  Specify SSL/TLS cipher suite (See openssl ciphers)
    -f protocol     Specify SSL/TLS protocol
                    (TLS1, TLS1.1, TLS1.2 or ALL)
```
### 三、使用
- 通过命令
```text
-n 请求总数
-c 并发数
-t 时间限制（秒）
-T 附加参数的类型
-p 附加文件的内容

ab -n 100 -c 100 xxxxxxx
ab -t 60 -c 100 xxxxxx
ab -t 60 -c 50 -T 'text/plain' -p p.txt xxxxxx

```