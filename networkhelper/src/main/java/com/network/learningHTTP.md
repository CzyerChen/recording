> 从前觉得我就是码代码的，那些网络的知识，知道有哪几层就行了，还用管网络里面怎么流转的吗，但是渐渐地，了解了一些前端，接触了架构，才发现需要对所有部件大体的认知，一个也不能差

> 不知所以然的事情，不是铤而走险，就是破绽百出

HTTP: HyperText Transfer Protocol 超文本传输协议

### 一、什么是TCP/IP
1.TCP/IP协议族，不是就是指TCP或者IP协议，它是指与它们相关的各种协议，包括有HTTP, FTP, DNS, TCP, UDP, IP, SNMP等等都属于TCP/IP协议族

- FTP File Transfer Protocol: 文件传输协议，传输文件
- DNS Domain Name System: 域名系统，域名解析，需要识别到用户输入的www.baidu.com对应的实际IP，通过DNS可以将域名与IP地址进行相互的转换
- TCP Transmission Control Prococol:传输控制协议，通过ACK确保数据的准确发送
- UDP User Data Protocol: 用户数据协议，不能确保数据的准确发送
- IP(Internet Protocol: 网际协议

2.TCP/IP分层：这就是我停留在网络层面的认知
- OSI标准中的7层，包括应用层、表示层、会话层、传输层、网络层、数据链路层、物理层
- TCP/IP协议族有4层，包括应用层、传输层、网络层、链路层

- 应用层：面向用户的一层，也就是说用户可以直接操作该层，该层决定了向用户提供应用服务时的通信活动，HTTP\FTP\DNS\SMTP\POP3都位于这一层
- 传输层：传输层在应用层的下方，应用层会将数据交付给传输层进行传输，此处TCP会通过三次握手四次挥手进行数据的传输，TCP/UDP在这一层
- 网络层：网络层在传输层的下方，来处理在网络上流动的数据包，IP在这一层，需要在网络线路中选择一条传输线路 IP\ARP\ICMP\HDLC
- 链路层：链路层就是负责最底层的数据传输啦，用于除了网络硬件部分，包括数据链路层和物理层，包含网卡、光纤、控制操作系统、硬件设备驱动、NIC等

3.TCP/IP协议中数据如何交互，如何封包解包
- 发送端，根据分层添加首部标识的方式，生成传输报文, 在HTTP工作开始之前，客户机（Web浏览器）首先要通过网络与服务器建立连接，该连接是通过TCP来完成的
    - 应用层接收到HTTP数据，添加TCP首部，发送到传输层
    - 传输层收到报文后，添加IP数据包，发送到网络层
    - 网络层收到报文后，添加网络架构后，发送到链路层
- 通过网络的发送， 接收端通过一层层删除首部，拿到指定的数据
    - 链路层拆除网络架构，传给网络层
    - 网络层拆除IP数据包，发送给传输层
    - 传输层拆除TCP首部，发送给应用层
    - 应用层获取对应的http数据

4.TCP协议非常有名的就是三次握手四次回收的爱情故事
- TCP协议正是依靠上面的机制，才确保了数据的传输，包括发送报文前和结束发送报文后
- 前提说明：
    - 最开始连接是属于CLOSED状态，主动连接的是客户端，被动打开连接的是服务端
    - 通信主要涉及几个字段ACK判断是否接收到请求，SYN同步位，seq序列号，ack确认序列号
    - TCP规定，SYN报文段（SYN=1的报文段）不能携带数据，但需要消耗掉一个序号
    - TCP规定，SYN报文段（SYN=1的报文段）不能携带数据，但需要消耗掉一个序号
- 以上是一些规则，接下来看三次握手流程：
    - TCP服务器进程先创建传输控制块TCB，准备接收请求，服务器就进入了LISTEN（监听）状态
    - (1)TCP客户进程先创建传输控制块TCB，然后向服务端发送请求报文：SYN=1,seq=m,客户端进入SYN-SENT（同步已发送状态）状态，消耗序列号
    - (2)TCP服务器收到请求报文后，同意连接，发出确认报文，ACK=1,SYN=1,ack=m+1,seq=n
    - (3)TCP客户进程收到确认后，还要向服务器给出确认,ACK=1,SYN=1,ack=n+1,不消耗序列号，客户端进入ESTABLISHED（已建立连接）状态
    - 服务端收到确认消息后也进入ESTABLISHED（已建立连接）状态
    - 最后一次握手主要防止已经失效的连接请求报文突然又传送到了服务器，从而产生错误
```text
建立TCB                          被动建立TCB
  client CLOSRD  --------(1)------------> server LISTEN
  
  client SYN-SENT <-------(2)------------- server LISTEN
  
  client SYN-SENT --------(3)------------> server SYN-RCVD
  
  client          <-----establish-------->  server
```
- TCP的四次挥手流程：
    - 一些规定： TCP规定，FIN报文段即使不携带数据，也要消耗一个序号
    - 包含的字段SYN FIN ack seq
    - 首先，双方都是ESTABLISH状态
    - (1)客户端进程发出连接释放报文，并且停止发送数据,FIN=1,seq=x,户端进入FIN-WAIT-1（终止等待1）状态
    - (2)服务器收到连接释放报文，发出确认报文,ACK=1，ack=x+1,seq=y,服务端就进入了CLOSE-WAIT（关闭等待）状态
    - 以上CLOSR_WAIT状态是很多连接应用在释放的时候，查看网络状态都会出现的问题
    - TCP服务器通知高层的应用进程，客户端向服务器的方向就释放了，这时候处于半关闭状态，即客户端已经没有数据要发送了，但是服务器若发送数据，客户端依然要接受
    - 客户端收到服务器的确认请求后,客户端就进入FIN-WAIT-2（终止等待2）状态
    - (3)服务器将最后的数据发送完毕后发送释放报文，FIN=1，ACK=1,seq=z,ack=y+1,服务器进入LAST-ACK（最后确认）状态，等待客户端的确认
    - (4)客户端收到服务器的连接释放报文后，必须发出确认，ACK=1，ack=z+1，自己的序列号是seq=x+1,客户端就进入了TIME-WAIT（时间等待）状态
    - 此时TCP连接还没有释放，须经过2∗ *∗MSL（最长报文段寿命）的时间后，客户端撤销相应的TCB后，才进入CLOSED状态
    - 服务端在此确认收到客户端的报文后，立即进入CLOSED状态，撤销TCB后，结束TCP连接
    - MSL（Maximum Segment Lifetime），TCP允许不同的实现可以设置不同的MSL值
    - 由于服务器会考虑最后一次的确认断开报文如果客户端没有收到，就会发送两次，因而客户端只有在过了这两次报文发送时间之后，才能断定是断开了
```text
  client <-----establish-------->  server
  主动关闭                        通知应用关闭-->被动关闭
  client FIN-WAIT1 --------(1)------------> server CLOSE-WAIT
                                 
  client FIN-WAIT2 <-------(2)------------- server CLOSE-WAIT
  
  client FIN-WAIT2 <--------(3)------------ server LAST-ACK
  
  client TIME-WAIT ---------(4)------------>  server CLOSED
  
  client 等待2MSL -> CLOSRD
```    


### 二、 什么是HTTP 
- http就是一种通信协议，是没有状态的，它不会记录上一个请求是谁发来的，不会对通信状态保存，这样就能大大提高http的时间处理，为了更快地处理大量事务，确保协议的可伸缩性
- http不够安全，通信使用明文，不验证通信方身份
- Http是属于应用层的协议，配合TCP/IP使用
- 但是我们在通信交互过程中，我们会想知道是谁发的消息，那就出现了cookie 技术
- 要是服务器端想要记住客户端是谁，那么就颁发一个cookie给客户端
- 客户端把Cookie保存在硬盘中，当下次访问服务器的时候，浏览器会自动把客户端的cookie带过去，这样就能通信了

### 三、HTTP 0.9/1.0/1.1/2.0，单次请求到持续连接，你跟上脚步了吗？
#### 1.持续连接 keep alive
- 在http1.0的时候，一次请求结束后就会断开，不会持续，当一次
页面需要请求大量数据，就要连续建立连接，断开链接，销毁资源，又申请资源，非常不利于资源的管理
- Http 1.1开始这个单次请求连接就就改成持续连接，能够一次加载多个请求的数据，是管道化的，是顺序的
- Http 2.0开始，这个持续连接的请求改成了并行化，不再是顺序的，一个请求有多个资源是并行去请求的
- HTTP/1.1 和一部分的 HTTP/1.0 想出了持久连接（HTTP Persistent Connections，也称为 HTTP keep-alive 或 HTTP connection reuse）的方法。持久连接的特点是，只要任意一端没有明确提出断开连接，则保持TCP连接状态

#### 2.好处
- 持久连接的好处在于减少了 TCP 连接的重复建立和断开所造成额外开销，减轻了服务器端的负载。另外，减少开销的那部分时间， HTTP 请求和响应能够更早地结束，这样Web页面的显示速度也就相应提高了。

#### 3.管线化
- 持续连接大都使用管线化技术，不同等待一个请求的返回就可以发送下一个请求
- 管线化的前提就是持久连接
- 请求流程:
```text
请求1 -> 请求2 -> 请求3 -> 响应1 -> 响应2 -> 响应3
```

### 四、http中的压缩技术
- 使用压缩技术把实体主体压小，在客户端把数据解析
- 使用分块传输编码，将实体主体分块传输，当浏览器解析到实体主体就能够显示
- 在下载东西的过程中断了，以前是只能够重新下载了，但是现在可以中断处继续下载。可以使用到获取范围数据，这是范围请求


### 五、HTTP的报文详情
- 报文的内容有人问，重要吗？这不仅是重要，也是基础的，需要会看，懂的是什么意思，有哪些字段，在服务器接收和发送请求的时候需要怎么放置参数

#### 1.请求报文
- HTTP报文包括：方法、URI、HTTP版本、HTTP首部字段等，由请求行、请求头、请求体、空行组成
- 请求行：POST /dynamix/content HTTP/1.1
- 请求体：请求参数
- 请求头，包括：
1.Accept: text/html,image/*    【浏览器告诉服务器，它支持的数据类型】

2.Accept-Charset: ISO-8859-1    【浏览器告诉服务器，它支持哪种字符集】

3.Accept-Encoding: gzip,compress 【浏览器告诉服务器，它支持的压缩格式】

4.Accept-Language: en-us,zh-cn 【浏览器告诉服务器，它的语言环境】

5.Host: www.it315.org:80【浏览器告诉服务器，它的想访问哪台主机】

6.If-Modified-Since: Tue, 11 Jul 2000 18:23:51 GMT【浏览器告诉服务器，缓存数据的时间】

7.Referer: http://www.it315.org/index.jsp【浏览器告诉服务器，客户机是从那个页面来的---反盗链】

8.User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)【浏览器告诉服务器，浏览器的内核是什么】

9.Cookie【浏览器告诉服务器，带来的Cookie是什么】

10.Connection: close/Keep-Alive  【浏览器告诉服务器，请求完后是断开链接还是保持链接】

11.Date: Tue, 11 Jul 2000 18:23:51 GMT【浏览器告诉服务器，请求的时间】

```text
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Cache-Control: max-age=0
Connection: keep-alive
Cookie: tvfe_boss_uuid=35f03e2b1355d809; pgv_pvi=1159542784; RK=CAGHq2GqUb; pac_uid=1_632307140; o_cookie=632307140; pgv_pvid=8873723360; _ga=GA1.2.1180943068.1509606102; ua_id=tbA3ey0jsYBcfF57AAAAAN6-PxNhjLNMFokx0MjSxY0=; xid=d1977e7b65ae5f496c386332e63e7287; mm_lang=zh_CN; ptcz=b3e098de4d51fb1cbf1ab71c10a9376f4fc929af8b930da87ee8446c081ea9d9
Host: mp.weixin.qq.com
If-Modified-Since: Wed, 20 Mar 2019 18:31:03 +0800
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36
```
- 请求方式有：
```text
GET：请求指定的页面信息，并返回实体主体。

HEAD：类似于get请求，只不过返回的响应中没有具体的内容，用于获取报头。

POST：向指定资源提交数据进行处理请求（例如提交表单或者上传文件）。数据被包含在请求体中。

PUT：从客户端向服务器传送的数据取代指定的文档的内容。

DELETE：请求服务器删除指定的页面。
```
- GET和POST的区别：
	- 其实大家基本都知道，GET用于获取请求内容，POST用于提交数据，GET有长度限制，POST没有长度限制
```text
GET在浏览器回退时是无害的，而POST会再次提交请求。

GET请求会被浏览器主动缓存，而POST不会，除非手动设置。

GET请求参数会被完整保留在浏览器历史记录里，而POST中的参数不会被保留。

GET请求在URL中传送的参数是有长度限制的，而POST没有限制。

GET参数通过URL传递，POST放在Request body中。
```	
#### 2.响应报文
- 响应报文由HTTP版本、状态码（数字和原因短语）、HTTP首部字段3部分组成
- 状态行
``text
格式： HTTP版本号　状态码　原因叙述
状态行：HTTP/1.1  200    OK
```Location: http://www.it315.org/index.jsp 【服务器告诉浏览器要跳转到哪个页面】

1.Server:apache tomcat【服务器告诉浏览器，服务器的型号是什么】

2.Content-Encoding: gzip 【服务器告诉浏览器数据压缩的格式】

3.Content-Length: 80 【服务器告诉浏览器回送数据的长度】

4.Content-Language: zh-cn 【服务器告诉浏览器，服务器的语言环境】

5.Content-Type: text/html; charset=GB2312 【服务器告诉浏览器，回送数据的类型】

6.Last-Modified: Tue, 11 Jul 2000 18:23:51 GMT【服务器告诉浏览器该资源上次更新时间】

7.Refresh: 1;url=http://www.it315.org【服务器告诉浏览器要定时刷新】

8.Content-Disposition: attachment; filename=aaa.zip【服务器告诉浏览器以下载方式打开数据】

9.Transfer-Encoding: chunked  【服务器告诉浏览器数据以分块方式回送】

10.Set-Cookie:SS=Q0=5Lb_nQ; path=/search【服务器告诉浏览器要保存Cookie】

11.Expires: -1【服务器告诉浏览器不要设置缓存】

12.Cache-Control: no-cache  【服务器告诉浏览器不要设置缓存】

13.Pragma: no-cache   【服务器告诉浏览器不要设置缓存】

14.Connection: close/Keep-Alive   【服务器告诉浏览器连接方式】

15.Date: Tue, 11 Jul 2000 18:23:51 GMT【服务器告诉浏览器回送数据的时间】

```text
Cache-Control: public, max-age=500
Connection: keep-alive
Content-Encoding: deflate
Content-Length: 39404
Content-Security-Policy: script-src 'self' 'unsafe-inline' 'unsafe-eval' http://*.qq.com https://*.qq.com http://*.weishi.com https://*.weishi.com 'nonce-1923562421';style-src 'self' 'unsafe-inline' http://*.qq.com https://*.qq.com;object-src 'self' http://*.qq.com https://*.qq.com;font-src 'self' data: http://*.qq.com https://*.qq.com http://fonts.gstatic.com https://fonts.gstatic.com;frame-ancestors 'self' http://wx.qq.com https://wx.qq.com http://wx2.qq.com https://wx2.qq.com  http://wx8.qq.com https://wx8.qq.com http://web.wechat.com https://web.wechat.com http://web1.wechat.com https://web1.wechat.com http://web2.wechat.com https://web2.wechat.com http://sticker.weixin.qq.com https://sticker.weixin.qq.com http://bang.qq.com https://bang.qq.com http://app.work.weixin.qq.com https://app.work.weixin.qq.com http://work.weixin.qq.com https://work.weixin.qq.com http://finance.qq.com https://finance.qq.com http://gu.qq.com https://gu.qq.com http://wzq.tenpay.com https://wzq.tenpay.com;report-uri https://mp.weixin.qq.com/mp/fereport?action=csp_report
Content-Type: text/html; charset=UTF-8
Content-Type: text/html; charset=UTF-8
Expires: Sun, 24 Mar 2019 19:23:52 +0800
Last-Modified: Sun, 24 Mar 2019 19:15:32 +0800
LogicRet: 0
MMLAS-VERIFYRESULT: CAE=
RetKey: 14
Set-Cookie: rewardsn=; Path=/
Set-Cookie: payforreadsn=EXPIRED; Path=/; Expires=Sat, 23-Mar-2019 11:15:32 GMT; HttpOnly
Set-Cookie: wxtokenkey=777; Path=/; HttpOnly
Strict-Transport-Security: max-age=0
```

#### 一次完整的请求头内容
```text
通用部分
Request URL: https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484979&idx=2&sn=abe78c7ce58c15cb8b2e26602802e096&chksm=ebd74732dca0ce24b00b10ed3948801bc1ab0fdfa3cdb478b21d1048a4e5564bbda2316b31bb&mpshare=1&scene=1&srcid=
Request Method: GET
Status Code: 200 OK
Remote Address: 180.163.21.166:443
Referrer Policy: no-referrer-when-downgrade

响应头部分
Cache-Control: public, max-age=500
Connection: keep-alive
Content-Encoding: deflate
Content-Length: 39404
Content-Security-Policy: script-src 'self' 'unsafe-inline' 'unsafe-eval' http://*.qq.com https://*.qq.com http://*.weishi.com https://*.weishi.com 'nonce-1923562421';style-src 'self' 'unsafe-inline' http://*.qq.com https://*.qq.com;object-src 'self' http://*.qq.com https://*.qq.com;font-src 'self' data: http://*.qq.com https://*.qq.com http://fonts.gstatic.com https://fonts.gstatic.com;frame-ancestors 'self' http://wx.qq.com https://wx.qq.com http://wx2.qq.com https://wx2.qq.com  http://wx8.qq.com https://wx8.qq.com http://web.wechat.com https://web.wechat.com http://web1.wechat.com https://web1.wechat.com http://web2.wechat.com https://web2.wechat.com http://sticker.weixin.qq.com https://sticker.weixin.qq.com http://bang.qq.com https://bang.qq.com http://app.work.weixin.qq.com https://app.work.weixin.qq.com http://work.weixin.qq.com https://work.weixin.qq.com http://finance.qq.com https://finance.qq.com http://gu.qq.com https://gu.qq.com http://wzq.tenpay.com https://wzq.tenpay.com;report-uri https://mp.weixin.qq.com/mp/fereport?action=csp_report
Content-Type: text/html; charset=UTF-8
Content-Type: text/html; charset=UTF-8
Expires: Sun, 24 Mar 2019 19:23:52 +0800
Last-Modified: Sun, 24 Mar 2019 19:15:32 +0800
LogicRet: 0
MMLAS-VERIFYRESULT: CAE=
RetKey: 14
Set-Cookie: rewardsn=; Path=/
Set-Cookie: payforreadsn=EXPIRED; Path=/; Expires=Sat, 23-Mar-2019 11:15:32 GMT; HttpOnly
Set-Cookie: wxtokenkey=777; Path=/; HttpOnly
Strict-Transport-Security: max-age=0

请求头部分
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Cache-Control: max-age=0
Connection: keep-alive
Cookie: tvfe_boss_uuid=35f03e2b1355d809; pgv_pvi=1159542784; RK=CAGHq2GqUb; pac_uid=1_632307140; o_cookie=632307140; pgv_pvid=8873723360; _ga=GA1.2.1180943068.1509606102; ua_id=tbA3ey0jsYBcfF57AAAAAN6-PxNhjLNMFokx0MjSxY0=; xid=d1977e7b65ae5f496c386332e63e7287; mm_lang=zh_CN; ptcz=b3e098de4d51fb1cbf1ab71c10a9376f4fc929af8b930da87ee8446c081ea9d9
Host: mp.weixin.qq.com
If-Modified-Since: Wed, 20 Mar 2019 18:31:03 +0800
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36

请求参数
__biz: MzI4Njg5MDA5NA==
mid: 2247484979
idx: 2
sn: abe78c7ce58c15cb8b2e26602802e096
chksm: ebd74732dca0ce24b00b10ed3948801bc1ab0fdfa3cdb478b21d1048a4e5564bbda2316b31bb
mpshare: 1
scene: 1
srcid: 
```
### 六、HTTP的请求方法与响应状态码
- 正常请求
200正常
204正常，无数据返回，无需刷新页面
206范围请求

- 重定向
301 永久重定向
302 页面转发
303 页面转发，get方式获取资源
304 发送附带请求，但不符合要求
305 使用代理。被请求的资源必须通过指定的代理才能被访问
307 页面转发 ，post请求不会变成get

- 请求出错
400 请求报文语法错误
401 需要认证身份
403 没有权限访问
404 服务器没有这个资源
406 请求的资源的内容特性无法满足请求头中的条件
408 请求超时
409 请求存在冲突
410 请求遗失
413 响应实体过大
417 在请求头 Expect 中指定的预期内容无法被服务器满足
420 方法失效
422 不可处理的实体

- 服务器系统出错
500 内部资源出错
503 服务器正忙


### 七、HTTP 代理原理及实现


### 八、什么是HTTPS
- https就是披了一层SSL认证的http协议，使用SSL建立安全的通信线路
- HTTPS使用的是共享密钥和公开私有密钥混合来进行加密的
- SSL认证是有专门的网站进行颁发的一个身份认证，基于第三方的认证机构来获取认可的证书中认证该服务器是否是合法的
- 这个第三方的数字证书正式在一次又一次对http内容加密的探索中出现的，对称加密、非对称加密都存在很多问题，第三方的数字证书很好的解决了这些问题
- 这个数字证书是第三方机构使用自己的私钥对服务器的公钥进行加密之后的密文，与当前https中的证书不同


HTTP详解(1)-工作原理

TCP、IP协议族之数字签名与HTTPS详解

【HTTP】HTTPS 原理详解

HTTPS协议详解(二)：TLS/SSL工作原理

HTTPS原理及交互过程
HTTPS 原理解析

http 和 https 有何区别？如何灵活使用？

HTTPS原理及OKHTTP对HTTPS的支持

HTTP/2 幕后原理








