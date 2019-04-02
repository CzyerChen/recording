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

0.Location: http://www.it315.org/index.jsp 【服务器告诉浏览器要跳转到哪个页面】

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

16.Strict-Transport-Security，HTTP Strict Transport Security，简称为HSTS，允许一个HTTPS网站，要求浏览器总是通过HTTPS来访问它，就是一些由于用户输入了http实际需要转到https的操作，从应用服务端转移到浏览器支持了，无需自己做请求重定向带来安全风险了。max-age指定的秒数内，当前网站所有请求都会被重定向为https
```text
Chrome内置了一个HSTS列表，默认包含Google、Paypal、Twitter、Linode等等服务。我们也可以在Chrome输入chrome://net-internals/#hsts，进入HSTS管理界面。在这个页面，你可以增加/删除/查询HSTS记录
```
17.X-Frame-Options :为了减少点击劫持（Clickjacking）而引入的一个响应头
```text
x-frame-options: SAMEORIGIN
DENY：不允许被任何页面嵌入；
SAMEORIGIN：不允许被本域以外的页面嵌入；
ALLOW-FROM uri：不允许被指定的域名以外的页面嵌入（Chrome现阶段不支持）；
```
18.X-XSS-Protection:响应头是用来防范XSS的。0：禁用XSS保护，1：启用XSS保护，1; mode=block：启用XSS保护，并在检查到XSS攻击时，停止渲染页面
19.X-Content-Type-Options，某些浏览器会启用MIME-sniffing来猜测该资源的类型，解析内容并执行，通过设置X-Content-Type-Options: nosniff 可以禁止浏览器类型猜测，可用于IE8+和Chrome


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
- Google响应头：
```text
x-content-type-options: nosniff
x-frame-options: SAMEORIGIN
x-xss-protection: 1; mode=block
```
- Twitter响应头：
```text
strict-transport-security: max-age=631138519
x-frame-options: SAMEORIGIN
x-xss-protection: 1; mode=block
```
- PayPal响应头：
```text
X-Frame-Options: SAMEORIGIN
Strict-Transport-Security: max-age=14400
```
- facebook响应头：
```text
strict-transport-security: max-age=60
x-content-type-options: nosniff
x-frame-options: DENY
x-xss-protection: 0
content-security-policy: default-src *;script-src https://*.facebook.com http://*.facebook.com https://*.fbcdn.net http://*.fbcdn.net *.facebook.net *.google-analytics.com *.virtualearth.net *.google.com 127.0.0.1:* *.spotilocal.com:* chrome-extension://lifbcibllhkdhoafpjfnlhfpfgnpldfl 'unsafe-inline' 'unsafe-eval' https://*.akamaihd.net http://*.akamaihd.net;style-src * 'unsafe-inline';connect-src https://*.facebook.com http://*.facebook.com https://*.fbcdn.net http://*.fbcdn.net *.facebook.net *.spotilocal.com:* https://*.akamaihd.net ws://*.facebook.com:* http://*.akamaihd.net https://fb.scanandcleanlocal.com:*;
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
