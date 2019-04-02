
### 七、HTTP 代理原理及实现

### 八、HTTP缓存机制
- 缓存机制分为两种，一个是强制缓存，一个是对比缓存
- 强制缓存下，如果缓存命中就返回直接缓存的内容，如果缓存没有命中就去服务器请求数据
- 对比缓存，不论缓存是否命中，都会去服务器请求数据
- 如果两者同时存在，强制缓存优先级高于对比缓存，如果缓存命中还是不会去请求服务器
- 浏览器会有Expires Last-Modified If-Modified-Since 字段标识过期，也就是页面有缓存，可以通过CTRL+F5强制刷新
- 可以设置Pragma: no-cache和Cache-control，标示拒绝使用缓存

#### 强制缓存
- 查看响应头中的Expires/Cache-Control
```text
access-control-allow-origin: *
cache-control: must-revalidate, max-age=31536000
content-encoding: gzip
content-length: 12690
content-type: application/x-javascript
date: Tue, 26 Mar 2019 07:20:25 GMT
expires: Wed, 25 Mar 2020 07:20:24 GMT
last-modified: Tue, 26 Mar 2019 07:10:00 GMT
server: NWSs
status: 200
x-cache-lookup: Hit From Disktank3 Gz
x-nws-log-uuid: b9bc0f4f-76b0-403e-8ffc-514a4ba1879a
```
```text
private:             客户端可以缓存
public:              客户端和代理服务器都可缓存
max-age=xxx:         缓存的内容将在 xxx 秒后失效,默认就是private
no-cache:           需要使用对比缓存来验证缓存数据
no-store:           所有内容都不会缓存
```
#### 对比缓存
- 对比缓存每次都会请求服务器，如果是本地没有缓存的，就会把全部信息请求过来，请求时间100ms，状态码200,如果本地有缓存，只返回给客户端header部分，说明缓存对比结束，已有缓存可用，请求时间60ms,状态码304
- 主要是这两个字段的对比，Last-Modified  /  If-Modified-Since
```text
Last-Modified: Tue, 26 Mar 2019 15:41:06 +0800
If-Modified-Since: Tue, 26 Mar 2019 15:27:32 +0800
Status Code: 200 OK
最后修改时间大于IF的时间：资源请求状态码200，获取最新数据

Status Code: 304 Not Modified
无Last-Modified
If-Modified-Since: Tue, 26 Mar 2019 15:51:58 +0800
没有最后修改时间，只有IF时间：资源请求状态码304，延用缓存的数据
```

### ETag
- Response Header 告诉浏览器当前资源在服务器的唯一标识
```text
cache-control: max-age=0, private, must-revalidate
content-encoding: gzip
content-security-policy: script-src 'self' 'unsafe-inline' 'unsafe-eval' *.jianshu.com *.jianshu.io api.geetest.com static.geetest.com dn-staticdown.qbox.me zz.bdstatic.com *.google-analytics.com hm.baidu.com push.zhanzhang.baidu.com res.wx.qq.com qzonestyle.gtimg.cn as.alipayobjects.com nbrecsys.4paradigm.com shared.ydstatic.com gorgon.youdao.com ;style-src 'self' 'unsafe-inline' *.jianshu.com *.jianshu.io api.geetest.com static.geetest.com shared.ydstatic.com ;frame-ancestors 'self' dig.chouti.com k.21cn.com ;
content-type: text/html; charset=utf-8
date: Tue, 26 Mar 2019 07:56:03 GMT
eagleid: b4a39f4815535869613706920e
etag: W/"efdb203bd4e92d77324221ec7bf11244"
server: Tengine
set-cookie: locale=zh-CN; path=/
set-cookie: _m7e_session_core=25b264acc52791d95e40b4b3573d30a4; domain=.jianshu.com; path=/; expires=Tue, 26 Mar 2019 13:56:03 -0000; secure; HttpOnly
status: 200
strict-transport-security: max-age=31536000; includeSubDomains; preload
timing-allow-origin: *
vary: Accept-Encoding
via: cache10.l2cm12-1[2414,0], cache26.l2et15-2[2439,0], cache8.cn497[2441,0]
x-content-type-options: nosniff
x-frame-options: ALLOW-FROM http://dig.chouti.com http://k.21cn.com/
x-request-id: 0a46e697-0f00-41d3-89d3-7e528c8993d0
x-runtime: 2.330969
x-xss-protection: 1; mode=block
```
### If-None-Match
- 再次请求服务器时,告知服务器客户端缓存数据的唯一标识
- 如果资源有改动，唯一标识会改变，对比不同，则返回200，加载所有数据
- 如果资源没有改动，唯一标识不变，对比一致，则返回304，返回Header信息，使用缓存
```text
:authority: www.jianshu.com
:method: GET
:path: /p/9a08417e4e84
:scheme: https
accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
accept-encoding: gzip, deflate, br
accept-language: zh-CN,zh;q=0.9,en;q=0.8
cache-control: max-age=0
cookie: __yadk_uid=6rDWRVhIvn81br92WCG1JXcV2e17iB2q; read_mode=day; default_font=font2; locale=zh-CN; signin_redirect=https%3A%2F%2Fwww.jianshu.com%2Fp%2Fef18af5a9c1d; remember_user_token=W1s5NzM1ODM1XSwiJDJhJDExJFpIR1NRc21BeDdjSndFMkVYR2J4ZWUiLCIxNTUzNTY5MzI3Ljc5ODU1NTYiXQ%3D%3D--6fc7fb0e002cefbab6a76dd849925408a665aaaf; _m7e_session_core=25b264acc52791d95e40b4b3573d30a4; Hm_lvt_0c0e9d9b1e7d617b3e6842e85b9fb068=1553494044,1553501964,1553502643,1553580143; Hm_lpvt_0c0e9d9b1e7d617b3e6842e85b9fb068=1553586965; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%229735835%22%2C%22%24device_id%22%3A%22167357296d2120-0606b7f356fd0e-6313363-2073600-167357296d3702%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_utm_medium%22%3A%22not-signed-in-user-follow-button%22%7D%2C%22first_id%22%3A%22167357296d2120-0606b7f356fd0e-6313363-2073600-167357296d3702%22%7D
if-none-match: W/"efdb203bd4e92d77324221ec7bf11244"
upgrade-insecure-requests: 1
user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36
```
#### 缓存流程
- 初次请求
```text
        浏览器请求
            |
            | 
          无缓存
            |
            |
向服务器返回数据，返回ETag,返回页面资源 
            |
            |
         请求响应
            |
           呈现

```
- 再次请求
```text
                浏览器请求
                    |
                    |
                缓存过期？-----是-------ETag?----有---If-None-Match---------|
                    |                   |                                 |
                   否                   无                                |
                    |                   |                                 |
                    |              Last Modified--有---If-None-Match------|
                    |                   |                                 |
                    |                   无                           服务器决策
                    |                   |                                 |
                    |              向服务器请求                         200 / 304
                    |                   |                                 |
                    |                   |                           |-----------|   
                    |                   |                          200         304
                    |           请求响应，返回全部数据 <---------------|           |
                    |                   |                                        |
                    |-------------------|------------使用缓存---------------------|
                                       \|/
                                       呈现
``` 
