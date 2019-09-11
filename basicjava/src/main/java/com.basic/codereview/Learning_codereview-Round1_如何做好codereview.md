### 一、好处
- 团队知识的共享
- 代码质量的提升
- 团队编码的规范
- 代码修改成本的降低

### 二、工具
- 很多源代码管理工具都自带Code Review工具，典型的像Github、Gitlab、微软的Azure DevOps，尤其是像Gitlab，还可以自己在本地搭建环境
- 或者一些专门的code review 工具： Facebook Phabricator & Google Gerrit
- [基于gitlab的code review介绍](https://www.cnblogs.com/ken-io/p/gitlab-code-review-tutorial.html)

### 三、擅做code review
- 每一次code review前，代码提交者需要做好充分自测
- 每一个code review需要做好评价的标签，来表示这个问题的优先级以及严重程度，提高效率
```$xslt
[blocker]: 在评论前面加上一个[blocker]标记，表示这个代码行的问题必须要修改
[optional]：在评论前面加上一个[optional]标记，表示这个代码行的问题可改可不改
[question]：在评论前面加上一个[question]标记，表示对这个代码行不理解，有问题需要问，被审查者需要针对问题进行回复澄清
```