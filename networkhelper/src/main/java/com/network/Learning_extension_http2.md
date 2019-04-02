### 十四、HTTP/2 幕后原理

参考[HTTP2](https://www.ibm.com/developerworks/cn/web/wa-http2-under-the-hood/index.html)

参考[http2-explain-chinese](http://blog.didispace.com/books/http2-explained-chinese/part1.html)

[目前支持 HTTP/2 的浏览器有 Chrome 41+、Firefox 36+、Safari 9+、Windows 10 上的 IE 11 和 Edge。服务器方面则有 F5、H2O、nghttp2 等数十种选择，各种语言实现的都有]

#### HTTP/2 和 HTTP/1.X的区别
1.HTTP 1.1的缺点
- http 1.1的规范繁多，可选项过多
- HTTP 1.1很难完全使用出TCP协议能提供的所有强大能力
- 由于UI的发展，网页的负载不断加重，网络延迟始终是大问题，网页视频、会议、游戏对低延迟的要求变高

2.克服http的延迟
- Spriting
```text
Spriting是一种将很多较小的图片合并成一张大图，再用JavaScript或者CSS将小图重新“切割”出来的技术
```
- inlining 内联
```text
Inlining是另外一种防止发送很多小图请求的技巧，它将图片的原始数据嵌入在CSS文件里面的URL里
```
- 拼接
```text
一些前端工具可以帮助开发人员将这些文件合并为一个大的文件，从而让浏览器能只花费一个请求就将其下载完，而不是发无数请求去分别下载那些琐碎的JavaScript文件

缺点：小文件的修改需要更新一整个页面
```
- 分片
```text
一个客户端最多只能对同一主机建立多个TCP连接来加载资源，虽然有上限，但是一定程度上，仍然是能够通过增加客户端到服务端的连接来减少响应延迟，据统计平均有40个连接
```
3.升级HTTP
```text
降低协议对延迟的敏感

修复pipelining和head of line blocking的问题

防止主机需求更高的连接数量

保留所有现有的接口，内容，URI格式和结构

由IETF的HTTPbis工作组来制定
```

4.区别
- 完全语义兼容 HTTP/1.1 
- 大幅度提升web性能
- 减少时延
- 采用多路复用，而不是之前的管道顺序的资源请求，多路复用允许同时通过单一的 HTTP/2 连接发起多重的请求-响应消息，提高了并行请求资源的能力
- 浏览器客户端在同一时间，针对同一域名下的请求有一定数量限制。超过限制数目的请求会被阻塞
- 线头阻塞问题，HTTP Pipelining其实是把多个HTTP请求放到一个TCP连接中一一发送，而在发送过程中不需要等待服务器对前一个请求的响应；只不过，客户端还是要按照发送请求的顺序来接收响应

#### 新特性
```text
新升级路径
二进制分帧
请求/响应复用
报头压缩
流优先化
服务器推送
流控制
```
1.新升级路径：对于基于 HTTP/2 的安全连接，无法通过升级标头请求切换协议（还不太明白）

2.二进制协议：这是性能增强的一个焦点，在应用层和传输层之间添加一个二进制分帧层，重新设计了编码机制，没有修改方法、动词和标头的熟悉语义
```text
在客户端与服务器之间建立了一个 TCP 连接,这个连接时持续打开状态
消息是通过逻辑流进行传递的
一条消息包含一个完整的帧序列。在经过整理后，这些帧表示一个响应或请求

Connection
|----------------------------------------------------------|
| Stream1            |---------------------------|         |
|                    |     Request Message       |         |
|   -----------------|           FRAME           |---->    |
|                    |---------------------------|         |
|            |------------------------------------|        |
|  <---------|   Response Message                 |-----   |
|            |    FRAME      FRAME     FRAME      |        |
|            |    FRAME      FRAME     FRAME      |        |
|            |------------------------------------|        |
|----------------------------------------------------------|

|----------------------------------------------------------|
| Stream2            |-----------|     |---------|         |
|                    |           |     |         |         |
|   -----------------|           |-----|         |-->      |
|                    |-----------|     |---------|         |
|            |--------|      |--------------------|        |
|  <---------|        |------|                    |-----   |
|            |        |      |                    |        |
|            |        |      |                    |        |
|            |--------|      |--------------------|        |
|----------------------------------------------------------|
以上我们能看出，消息传递是持久的，并且可以多个响应合并返回
```
- 基本概念
```text
连接和流：两个端点见建立连接后，可以在这个连接上传输多个流，流可以交织，因而可以多个并行快速传输

消息：消息是一组帧，通过重建帧，可以将帧还原为完整的请求或相应，一组请求和响应只会在一个特定流上传输

帧：帧是通信的基本单位，每个帧有一个标头，其中包含帧的长度和类型、一些布尔标志、一个保留位和一个流标识符

长度：帧的长度，最大长度2^24 =16M,默认长度2^14=16K

类型：标识帧的用途，
HEADERS：帧仅包含 HTTP 标头信息
DATA：帧包含消息的所有或部分有效负载
PRIORITY：指定分配给流的重要性
RST_STREAM：错误通知：一个推送承诺遭到拒绝。终止流
SETTINGS：指定连接配置
PUSH_PROMISE：通知一个将资源推送到客户端的意图
PING：检测信号和往返时间
GOAWAY：停止为当前连接生成流的停止通知
WINDOW_UPDATE：用于管理流的流控制
CONTINUATION：用于延续某个标头碎片序列

标志：flag字段是布尔值，标识帧的状态
DATA帧：END_STREAM表示数据流结束，PADDED表示存在填充数据
HEADERS 帧：可以将相同的标志指定为 DATA 帧，并添加两个额外的标志：END_HEADERS 和 PRIORITY，前者表示标头帧结束，后者表示设置了流优先级。
PUSH_PROMISE 帧：可以设置 END_HEADERS 和 PADDED 标志
其他帧没有标志

流标识符:用于跟踪逻辑流的帧成员关系,成员每次仅属于一条消息和流
```
3.请求响应复用
- 例子：体现连接复用和流的交织
```text 
Browser   (<- Stream1 HEADERS)(<- Stream2 HEADERS)( Stream3 DATA ->)(<- Stream2 DATA)(Stream3 HEADERS->)(<- Stream1 DATA)  Server

其实上面的流是两个方向的消息交织在一起了

Browser   (<- Stream1 HEADERS)(<- Stream2 HEADERS)(<- Stream2 DATA)(<- Stream1 DATA)  Server
                                   ( Stream3 DATA ->)(Stream3 HEADERS->)
                                   
共发送Stream1 Stream2 Stream3,所有帧在另一端重新组装，以形成完整的请求或响应消息
```
- 帧交织的好处：
```text
所有请求和响应都在一个套接字TCP上发生
 
所有响应或请求都无法相互阻塞
 
减少了延迟
 
提高了页面加载速度
 
消除了对 HTTP 1.1 工具的需求
```
- HTTP 请求映射到HTTP2的帧数据上
```text
HTTP 请求                                    Frame 二进制帧
GET /index.html  HTTP1.1                     HEADERS
Host: example.com                             +END_STREAM      ---- 加号标识END_STREAM为true,表明是该请求的最后一帧
Accept:text/html                              +END_HEADERS     ---- 加号标识END_HEADERS为true，表明该帧是流中最后一个包含标头信息的帧
                                               :method：GET
                                               :schema:http
                                               :path: /index.html
                                               :authority: example.com
                                               :accept:text/html
                                               
一个完整的请求，包含header和data
HTTP 请求                                    Frame 二进制帧
HTTP1.1 200 OK                               HEADERS
Content-Length:11                            -END_STREAM      
Content-Type:text/html                       +END_HEADERS    
hello world                                    :status:200
                                               :content-length:11
                                               :content-type: text/html
                                             DATA
                                              +END_STREAM
                                              hello world
```
4.报头压缩
- HTTP/2协议有HPACK，用于减少客户端和服务器相应之间标头信息的重复开销
- 要求客户端和服务器都维护之前看见的标头字段的列表，未来在构建引用了已看见标头列表的消息时可以使用此列表
- 同一波请求流上，特别是网页项目，一次加载会是一个页面上的内容，因而请求头的大部分都是重复的
```text
两个请求：
HTTP REQUESR1    :method GET :schema https :host example.com :path /index.html :authority example.org  :accept text/html :user-agent Mozilla/5.0

HTTP REQUESR2    :method GET :schema https :host example.com :path /info.html :authority example.org  :accept text/html :user-agent Mozilla/5.0

 ||
\||/
 \/
将统一连接上的两个请求头压缩
HEADERS frame Stream1    :method GET :schema https :host example.com :path /index.html :authority example.org  :accept text/html :user-agent Mozilla/5.0

HEADERS frame Stream2   :path /info.html 

第二个请求发送，只需要发送与上一个请求不同的部分，因为服务器端保留着可见的标头列表
```
5.流优先化
- 通过一个0-256之间的数字，标识一个流的优先级，可以决定请求资源是否优先响应，可以将优先级组合合并到一个依赖树中，允许资源加载的相互依赖
- A是顶层流，A有BC两个子流，如果B拥有40%资源，C就拥有60%资源，DE也可以是C的子资源，共享C的60%资源
- 流优先级是对服务器的建议，允许服务动态调整资源

6.服务器推送
- 这是比较显著的改变，以往的请求都是客户端主动发起，服务端是对客户端的响应，200 OK
- 在HTTP2中，服务端可以预先给客户端推送一些数据，比如首页数据的加载，首页页面的渲染，如果服务端预先推送给发起连接的网页，当下一次资源如果没有变动，客户端发起主动请求，只会快速响应304，标识页面已有更新并且没有变动，大大减小了正常连续请求中DATA的大小，而提升网页响应
- HTTP/2 如何管理服务器推送而不会让客户端过载？针对希望发送的每个资源，服务器会发送一个 PUSH_PROMISE 帧，但客户端可通过发送 RST_STREAM 帧作为响应来拒绝推送（如果浏览器的缓存中已包含该资源）。重要的是所有 PUSH_PROMISE 都在响应数据之前发送，所以客户端知道它需要请求哪些资源

7.流控制
- 流控制管理数据的传输，允许接收者停止或减少发送的数据量。例如网页查看一个视频，点击暂停，视频流就可以停止传输，避免耗尽网页缓存
- 打开一个连接后，服务器和客户端会立即交换 SETTINGS 帧来确定流控制窗口的大小。默认情况下，该大小设置为约 65 KB，但可通过发出一个 WINDOW_UPDATE 帧为流控制设置不同的大小

8.重置
- 和http相比，当http1.1发送了一个固定内容的请求之后很难中断，如果强制关闭则浪费了一个宝贵的HTTP资源，还需要通过三次握手新建一个TCP请求
- 在http1中可以在头信息中发送RST_STREAM实现消息的重置


#### [性能提升的原因](https://www.zhihu.com/question/34074946)
- 并没有改动请求头等内部的语义、方法、状态码、URI等，关键在在 应用层(HTTP/2)和传输层(TCP or UDP)之间增加一个二进制分帧层（Binary Framing）
- 在二进制分帧层，http/2会将所有的传输信息分割成更小的消息和帧，并对它们采取二进制格式的编码，首部信息被封装到HEADER frame,Request Body被封装到DATA frame
- HTTP/2 通信都在一个连接上完成，这个连接可以承载任意数量的双向数据流
- HTTP/1.x由于TCP的慢启动使得突发性、短时性的HTTP连接就会变得很低效（"线头阻塞"），HTTP/2的公用一个TCP连接的改善很好的利用了珍贵的TCP连接

```text
1.单连接多资源的方式，减少服务端的链接压力,内存占用更少,连接吞吐量更大

2.由于 TCP 连接的减少而使网络拥塞状况得以改善，同时慢启动时间的减少,使拥塞和丢包恢复速度更快

3.首部压缩，HTTP/2 使用了专门为首部压缩而设计的 HPACK 算法

4.服务端推送Service Push ，是一种在客户端请求之前就发送数据的机制，能够很好的利用浏览器缓存，缓存之前也讲过，304情况下数据的请求会少很多

```


### 十五、其他--- HTTP2技术扩展
#### [更多丰富内容](https://www.zhihu.com/question/34074946)
- 扩展：
http2协议强制规定了接收方必须读取并忽略掉所有未知帧（即未知帧类型的帧）。双方可以在逐跳原则（hop-by-hop basis）基础上协商使用新的帧，但这些帧的状态无法被改变，也不受流控制

- 备选服务：
服务器将会通过发送Alt-Svc头（或者http2的ALTSVC帧）来告知客户端另一个备选服务。即另外一条指向不同的服务源、主机或端口，但却能获取同样内容的路由。客户端应该尝试异步的去连接到该服务，如果连接成功的话，即可以使用该备选服务。

- Firefox 里的HTTP2
```text
从发布于2015年1月13日的Firefox 35之后，http2支持是默认开启的。

在地址栏里进入'about:config'，再搜索一个名为“network.http.spdy.enabled.http2draft”的选项，确保它被设置为true。Firefox 36添加了一个“network.http.spdy.enabled.http2”的配置项，并默认设置为true。后者控制的是“纯”http2版本，而前者控制了启用／禁用通过http2草案版本进行协商。从Firefox 36之后，这两者都默认为true。

Firefox只在TLS上实现了http2。只会看到http2只在https://的网站里得到支持

```

- Chromium 里的HTTP2
```text
2015年1月27日发布的Chrome 40起，http2已经默认为一些用户启用该功能

在地址栏里进入chrome://flags/#enable-spdy4，如果没有被enable的话，点击"enable"启用它

Chrome只在TLS上实现了http2。只会在以https://做前缀的网站里得到http2的支持

HTTP 2 ui:https://chrome.google.com/webstore/detail/http2-and-spdy-indicator/mpbpobfflnpcgagjijhmgnchggcjblin

```

- Curl 里的HTTP2
```text
curl项目从2013年9月就开始对http2提供实验性的支持

curl使用一个叫做nghttp2的库来提供http2帧层的支持。curl依赖于nghttp2 1.0以上版本

命令行使用：必须使用--http2参数来让curl使用http2，默认HTPP1.1

libcurl 参数
启用HTTP2:可以通过将curl_easy_setopt的SURLOPT_HTTP_VERSION参数设置为CURL_HTTP_VERSION_2来使libcurl尝试使用http2
多路复用：通过CURLMOPT_PIPELINING参数为你的程序启用HTTP/2多路复用功能
服务器推送：通过在CURLMOPT_PUSHFUNCTION参数中设定一个推送回调来激活该功能


```

- [http2中的协商机制](https://imququ.com/post/protocol-negotiation-in-http2.html)
```text
HTTP Upgrade 
例子:
GET / HTTP/1.1
Host: example.com
Connection: Upgrade, HTTP2-Settings
Upgrade: h2c
HTTP2-Settings: <base64url encoding of HTTP/2 SETTINGS payload>

提供Upgrade字段，HTTP/2 的协议名称是 h2c代表 HTTP/2 ClearText，如果服务端不支持http2， 会忽略该字段，使用HTTP1.1响应请求
不支持：
HTTP/1.1 200 OK
Content-Length: 243
Content-Type: text/html

支持：
HTTP/1.1 101 Switching Protocols
Connection: Upgrade
Upgrade: h2c
[ HTTP/2 connection ... ]
回应101状态码

```
- ALPN 应用层协商协议
由于SPDY被HTTP2取代，NPN(下一代协商机制)也修订为ALPN，在客户端和服务端建立连接的过程中，会在ALPN中列出自己支持的协议
```text
ALPN Protocol 
    ALPN String length:2
    ALPN Next Protocol : h2
    ALPN String length:8
    ALPN Next Protocol : spdy/3.1
    ALPN String length:8
    ALPN Next Protocol : http/1.1

```

- [使用H2O部署HTTP2服务](https://imququ.com/post/http2-resource.html)

- [使用 Nginx 部署 HTTP/2 服务](https://imququ.com/post/http2-resource.html)

- [使用apache 部署HTTP/2服务](https://www.v2ex.com/t/228371)

- [chrome指示器扩展插件](https://chrome.google.com/webstore/detail/http2-and-spdy-indicator/mpbpobfflnpcgagjijhmgnchggcjblin)

- [chrome自带的http/2查看工具](chrome://net-internals/#http2)
```text
HTTP/2 Enabled: true
ALPN Protocols: h2,http/1.1

标识字段有:
Host：xxxxx:443

Proxy：127.0.0.1：63430 

ID:277163 

Negotiated Protocol 协商的机制：h2  ---- http2

Active stream2 :1  ---- 存在的流

Unclaimed push 0

Max :128   ---- 最大128K？？

Initiated: 3

Pushed :1

Pushed and claimed 1 

Abandoned : 0 ---- 客户端拒绝，不支持服务端主动推送或者已有缓存

Received frames: 21 ---- 接收到从服务端发来的二进制帧frame

Secure : null

Received settings: null

Send window : 2147483647

Receive window : 15728640

Unchecked received data :29625

Error:0

点击sessionId,可以查看HTTP/2的流量
278413: HTTP2_SESSION
imququ.com:443 (PROXY 127.0.0.1:63430)
Start Time: 2019-04-02 16:04:04.618

t=3919 [st=   0] +HTTP2_SESSION  [dt=?]
                  --> host = "imququ.com:443"
                  --> proxy = "PROXY 127.0.0.1:63430"
t=3919 [st=   0]    HTTP2_SESSION_INITIALIZED
                    --> protocol = "h2"
                    --> source_dependency = 278400 (PROXY_CLIENT_SOCKET_WRAPPER)
t=3919 [st=   0]    HTTP2_SESSION_SEND_SETTINGS
                    --> settings = ["[id:1 (SETTINGS_HEADER_TABLE_SIZE) value:65536]","[id:3 (SETTINGS_MAX_CONCURRENT_STREAMS) value:1000]","[id:4 (SETTINGS_INITIAL_WINDOW_SIZE) value:6291456]"]
t=3919 [st=   0]    HTTP2_SESSION_UPDATE_RECV_WINDOW
                    --> delta = 15663105
                    --> window_size = 15728640
t=3919 [st=   0]    HTTP2_SESSION_SEND_WINDOW_UPDATE
                    --> delta = 15663105
                    --> stream_id = 0
t=3919 [st=   0]    HTTP2_SESSION_SEND_HEADERS
                    --> exclusive = true
                    --> fin = true
                    --> has_priority = true
                    --> :method: GET
                        :authority: imququ.com
                        :scheme: https
                        :path: /post/http2-traffic-in-wireshark.html
                        cache-control: max-age=0
                        upgrade-insecure-requests: 1
                        user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36
                        accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
                        accept-encoding: gzip, deflate, br
                        accept-language: zh-CN,zh;q=0.9,en;q=0.8
                        cookie: u=df0da0e0-418c-4369-89b2-247c794e6706; v=RRzRSR
                    --> parent_stream_id = 0
                    --> source_dependency = 278412 (HTTP_STREAM_JOB)
                    --> stream_id = 1
                    --> weight = 256
t=4493 [st= 574]    HTTP2_SESSION_RECV_SETTINGS
t=4493 [st= 574]    HTTP2_SESSION_SEND_SETTINGS_ACK
t=4493 [st= 574]    HTTP2_SESSION_RECV_SETTING
                    --> id = "3 (SETTINGS_MAX_CONCURRENT_STREAMS)"
                    --> value = 128
t=4493 [st= 574]    HTTP2_SESSION_UPDATE_STREAMS_SEND_WINDOW_SIZE
                    --> delta_window_size = 1
t=4493 [st= 574]    HTTP2_SESSION_RECV_SETTING
                    --> id = "4 (SETTINGS_INITIAL_WINDOW_SIZE)"
                    --> value = 65536
t=4493 [st= 574]    HTTP2_SESSION_RECV_SETTING
                    --> id = "5 (SETTINGS_MAX_FRAME_SIZE)"
                    --> value = 16777215
t=4493 [st= 574]    HTTP2_SESSION_RECV_WINDOW_UPDATE
                    --> delta = 2147418112
                    --> stream_id = 0
t=4493 [st= 574]    HTTP2_SESSION_UPDATE_SEND_WINDOW
                    --> delta = 2147418112
                    --> window_size = 2147483647
t=4493 [st= 574]    HTTP2_SESSION_RECV_SETTINGS_ACK
t=4508 [st= 589]    HTTP2_SESSION_RECV_PUSH_PROMISE
                    --> :method: GET

```
- [使用Wireshark调试HTTP/22流量](https://imququ.com/post/http2-traffic-in-wireshark.html)
[下载地址](https://www.wireshark.org/download/automated/)
```text
不能解密https的数据，会看到一串二进制的字符串

抓包原理:
直接读取并分析网卡数据
如果想解读HTTPS的数据：
1）如果你拥有 HTTPS 网站的加密私钥，可以用来解密这个网站的加密流量；
2）某些浏览器支持将 TLS 会话中使用的对称密钥保存在外部文件中，可供 Wireshark 加密使用

可以通过允许TSL会话中的对称密钥保存外部文件中，然后再Wireshark导入密钥，是其能够正确解析处HTTPS中的加密内容

但 Firefox 和 Chrome 只会在系统环境变量中存在 SSLKEYLOGFILE 路径时才会生成它，先来加上这个环境变量
mkdir ~/tls && touch ~/tls/sslkeylog.log

#zsh
echo "\nexport SSLKEYLOGFILE=~/tls/sslkeylog.log" >> ~/.zshrc && source ~/.zshrc

#bash
echo "\nexport SSLKEYLOGFILE=~/tls/sslkeylog.log" >> ~/.bash_profile && . ~/.bash_profile

在 Wireshark 的 SSL 配置面板的 「(Pre)-Master-Secret log filename」选项中选择这个导出的文件
「SSL debug file」也建议配上
```

- Fiddler 开启本地代理抓包，能够解密HTTPS的信息
```text
Fiddler 作为客户端跟服务端建立 TLS 连接，使用服务端的证书，处理请求和响应；
然后 Fiddler 又作为服务端跟浏览器建立 TLS 连接，使用 Fiddler 的证书，处理请求和响应。
所以 Fiddler 要解密 HTTPS 流量，需要先把它生成的根证书添加到系统受信任的根证书列表之中
```

- [http/2 与WEB性能优化](https://imququ.com/post/http2-and-wpo-1.html)


