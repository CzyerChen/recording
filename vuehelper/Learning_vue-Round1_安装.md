> 小白成长手册

> 由于长期没有使用vue,之前使用vue脚手架也是一年前了，当时文档居然没有记下来，踩得坑居然没有记下来，真是非常恼火

> 还记得上一次也是因为环境或者是版本吃了很多教训，这次果然，一年之后，版本有了很多改变，尝试了一把新的，又遇到了很多问题

> 换了mac的操作系统，也没有很熟悉，这次认真记录一下
- 背景说明
```text
├── cnpm@6.1.0
├── npm@6.9.0
├── vue-cli@3.8.4
└── webpack@4.35.0

node环境：
8.0.0
8.16.0
12.4.0

```
### 一、安装前提，删掉所有历史存在的node数据
- 卸载已安装到全局的 node/npm
```text
全局卸载node:
npm ls -g --depth=0 #查看已经安装在全局的模块，以便删除这些全局模块后再按照不同的 node 版本重新进行全局安装

sudo rm -rf /usr/local/lib/node_modules #删除全局 node_modules 目录
sudo rm /usr/local/bin/node #删除 node
cd  /usr/local/bin && ls -l | grep "../lib/node_modules/" | awk '{print $9}'| xargs rm #删除全局 node 模块注册的软链
```
### 二、运用nvm，做好node的版本管理
#### 2.1 为什么要用nvm？它有什么好处？
- 当项目开发版本多，版本升级，或者多版本多环境开发的时候，做好版本的控制和管理就非常重要，就像我们常用的maven就是这个理念
- Node Version Manager（Node版本管理器），用它可以方便的在机器上安装并维护多个Node的版本

#### 2.2 怎么用nvm?有哪些基础命令？
-  安装nvm,由于brew的一些问题，不建议使用brew install nvm来装（个人装过，出现重启terminal,居然命令还是找不到）
```text
## 直接装了最新版
apt-get install curl
curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
```
- 安装完毕，查看一下是否可用
```text
nvm ls-remote
这个命令用于查看远程仓库的node的所有版本，如果能出来结果，就证明nvm装好了
```
- 尝试安装node
```text
nvm install stable // 安装最新稳定版本 我当时就是v12.4.0

nvm install 8.16.0 // 之前用的是8系，因而装了8系的最新版本
nvm install 8.0.0  //装了一个老版
```
- 切换node的版本
```text
nvm use 8.0.0
nvm use 8.16.0
nvm use 12.4.0

这样大致是可以的
但是mac会提示一些warning ,让人很难受
nvm is not compatible with the npm config "prefix" option: currently set to "/Users/fabian/.nvm/versions/node/v0.12.7"
Run `nvm use --delete-prefix v4.6.2` to unset it.

那就按照它说的做吧，还有提示需要使用--silent ，反正按照它的提示做就行，nvm use --delete-prefix v4.6.2
```
- 查看当前版本node,当然你可以使用node -v
```text
nvm current
```
- 查看仓库安装的所有node版本
```text
nvm ls
         v8.0.0
        v8.16.0
->      v12.4.0
default -> stable (-> v12.4.0)
node -> stable (-> v12.4.0) (default)
stable -> 12.4 (-> v12.4.0) (default)
iojs -> N/A (default)
lts/* -> lts/dubnium (-> N/A)
lts/argon -> v4.9.1 (-> N/A)
lts/boron -> v6.17.1 (-> N/A)
lts/carbon -> v8.16.0
lts/dubnium -> v10.16.0 (-> N/A)

详情
nvm ls -l

```
- 使用nvm alias default <version>命令来指定一个默认的node版本

#### 2.3 其他操作
- 发现nvm下node很慢怎么办？
```text
把环境变量 NVM_NODEJS_ORG_MIRROR, 那么我建议你加入到 .bash_profile 文件中:
vim ~/.bash_profile
# nvm
export NVM_NODEJS_ORG_MIRROR=https://npm.taobao.org/mirrors/node

source ~/.bash_profile
```
- nvm安装完毕，不想关闭不想重启，能直接用吗？可以的，修改一下环境变量
```text
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
```
#### 2.4 常用命令
```text
nvm install <version> ## 安装指定版本，可模糊安装，如：安装v4.4.0，既可nvm install v4.4.0，又可nvm install 4.4
nvm uninstall <version> ## 删除已安装的指定版本，语法与install类似
nvm use <version> ## 切换使用指定的版本node
nvm ls ## 列出所有安装的版本
nvm ls-remote ## 列出所以远程服务器的版本（官方node version list）
nvm current ## 显示当前的版本
nvm alias <name> <version> ## 给不同的版本号添加别名
nvm unalias <name> ## 删除已定义的别名
nvm reinstall-packages <version> ## 在当前版本node环境下，重新全局安装指定版本号的npm包

```
#### 2.5 以上步骤，就安装好了nvm node npm

### node安装完毕，安装vue-cli,webpack
#### 要用npm安装东西，很慢或者失败怎么办？
- 安装淘宝镜像
- 一次安装
```text
npm --registry=https://registry.npm.taobao.org  install cnpm -g
```
- 永久安装
```text
npm config set registry https://registry.npm.taobao.org --global
or
npm config set disturl https://npm.taobao.org/dist --global
```
#### 全局安装webpack
- sudo cnpm install webpack -g

#### 安装vue 这边已经是vue-cli 3.8.4
- 第一次安装了2.9.4：sudo cnpm install vue-cli -g，vue init的project description就卡住了，即使升版本12.4.0，降版本8.0.0都没有用，试了一下午，反反复复，决定放弃2.x了
- 由于安装了2.9.4，又要卸载
```text
Vue CLI 的包名称由 vue-cli 改成了 @vue/cli。 

如果你已经全局安装了旧版本的 vue-cli(1.x 或 2.x)，

你需要先通过 npm uninstall vue-cli -g 或 yarn global remove vue-cli 卸载它
```
- 虽然在上述卸载的流程中，由于很多权限问题、文件打开问题、文件内解析问题，可能导致卸载有一些问题，但是，我重新安装了vue-cli3，居然没有冲突，这可能是全程最顺利的地方了
- 安装3.x
```text
cnpm install -g @vue/cli  ---- 安装3.x
# OR
yarn global add @vue/cli  -----需要有yarn命令
```
- 然后使用vue -V 检测到3.8.4版本，没有问题
- vue create demo 开始构建项目，最让人惆怅了，幸好全程没有问题
- 按照提示来选择，回车是确定，i,a,space都是代表了不同的操作，上下键进行游标的转换
```text
Vue CLI v3.8.4
? Please pick a preset: Manually select features  //选择自定义特性
? Check the features needed for your project: TS, PWA, Router, Vuex, CSS Pre-pro  //选择一些组件，省的后面装了
cessors, Linter
? Use class-style component syntax? Yes
? Use Babel alongside TypeScript (required for modern mode, auto-detected polyfi  //这个按照自己需求选
lls, transpiling JSX)? Yes
? Use history mode for router? (Requires proper server setup for index fallback //这个按照自己需求选
in production) Yes
? Pick a CSS pre-processor (PostCSS, Autoprefixer and CSS Modules are supported //这个按照自己需求选
by default): Less
? Pick a linter / formatter config: Prettier  //这个按照自己需求选
? Pick additional lint features: (Press <space> to select, <a> to toggle all, <i  //这个按照自己需求选
> to invert selection)Lint on save
? Where do you prefer placing config for Babel, PostCSS, ESLint, etc.? In dedica  //这个按照自己需求选
ted config files
? Save this as a preset for future projects? Yes  //这个按照自己需求选
? Save preset as: admin  //这个按照自己需求选
```
- 一旦这个配置好了，会记住你的一套配置，下次都可以直接使用这个配置来Init 一个项目
- [步骤参考](https://blog.csdn.net/qq_36407748/article/details/80739787)
- [参数解释参考](https://segmentfault.com/a/1190000014627083)
- 至此，也就好了

### 其他
#### 查看npm的默认配置
- npm config ls

#### mac安装性的很讨厌的东西
- 需要将node的执行目录修改到当前用户目录下，不然后期操作，mac会有很多的权限问题，主要就是与mac自身的一些命令的安全性考虑
- 很多很多操作，要写入配置、读取配置，都会permission deny,默认路径是/usr/local/lib/node_modules下面，就是很麻烦在/usr/local/lib

- 1.在用户的目录下，创建~/.npm-global
- 2.修改新的全局路径的位置  npm config set prefix '~/.npm-global'
- 3.修改环境变量 
```text
vim ~/.bash_profile

export PATH=~/.npm-global/bin:$PATH

source ~/.bash_profile

```
- 然后就可以了，可以自己尝试下一个包试试，就被安装进新目录了，没有权限困扰了
  
#### 使用 .nvmrc 文件配置项目所使用的 node 版本
```text
如果你的默认 node 版本（通过 nvm alias 命令设置的）与项目所需的版本不同，则可在项目根目录或其任意父级目录中创建 .nvmrc 文件，在文件中指定使用的 node 版本号，例如：
cd <项目根目录>  #进入项目根目录
echo 4 > .nvmrc #添加 .nvmrc 文件
nvm use #无需指定版本号，会自动使用 .nvmrc 文件中配置的版本
node -v #查看 node 是否切换为对应版本
```

#### nvm 与 n 的区别
```text
node 版本管理工具还有一个是 TJ 大神的 n 命令，n 命令是作为一个 node 的模块而存在，而 nvm 是一个独立于 node/npm 的外部 shell 脚本，因此 n 命令相比 nvm 更加局限。

由于 npm 安装的模块路径均为 /usr/local/lib/node_modules ，当使用 n 切换不同的 node 版本时，实际上会共用全局的 node/npm 目录。 因此不能很好的满足『按不同 node 版本使用不同全局 node 模块』的需求

```
     
### 参考
- [1](https://blog.csdn.net/ytangdigl/article/details/75095787)
- [2](https://www.cnblogs.com/ljq66/articles/10011444.html)
- [3](https://www.cnblogs.com/cllgeek/p/6076280.html)
- [4](https://segmentfault.com/a/1190000019500608)
- [5](https://blog.csdn.net/qq_36407748/article/details/80739787)


