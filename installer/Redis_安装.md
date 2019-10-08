### Windows: redis bin zip 包安装
- 在GitHub下载https://github.com/microsoftarchive/redis/releases
- 下载了zip包之后解压，进入bin目录
- 非服务化启动：redis-server.exe redis.windows.conf
- 服务化启动：redis-server.exe --service-install redis.windows.conf --service-name redis-server
- win + R 然后输入services.msc,可以看到服务的列表，如果Redis尚未启动，手动启动一下


### Linux redis 
- [参考](https://www.jb51.net/article/162493.htm)