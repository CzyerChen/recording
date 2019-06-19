#### git tag
```text
创建标签
##轻量级标签
git tag v1.0 -light

## 附注标签
git tag -a v1.0 -m "1.0 version"

查看标签
git tag
git tag -l 'v1.0.*' //匹配对应的标签
git tag show //展示标签版本信息

切换标签
git checkout v1.0

删除标签
git tag -d v1.0

补打标签,指定某一个commit
git tag -a v1.0 commitid -- 这个可以来git log里面看到

发布标签
git push origin v1.0

将本地标签全部提交
git push origin --tags

```
