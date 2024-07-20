# 豆芽办公

豆芽办公项目，后端基于Spring，前端基于Vue。

功能：
- 权限管理
  - 用户管理、角色管理、菜单管理 
  - 基于JWT的用户认证和授权
- 审批管理
  - 审批类型管理、审批模板管理、审批流程管理
  - 基于Activiti实现工作流管理
- 微信公众号
  - 审批的查看、发起和处理、公众号菜单管理
  - 消息推送
  - 微信授权登录

## 技术栈

- 基础框架：SpringBoot
- 数据缓存：Redis
- 数据库：MySQL
- 权限控制：SpringSecurity
- 工作流引擎：Activiti
- 前端技术：Vue + ElementUI + Axios

## 部署项目

### 部署MySQL

```shell
docker run --name mysql \
-e MYSQL_ROOT_PASSWORD=root \
-p 3306:3306 \
-v /my/datadir:/var/lib/mysql \
-v /my/confdir:/etc/mysql/conf.d \
-d mysql:8.0.38 \
--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

进入容器修改root用户权限以开启远程访问：

```sql
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
flush privileges;
```

### 部署Redis

```shell
docker run --name redis -p 6379:6379 -d --restart=always redis:bookworm
```

### 启动后端服务器

1. 建表
2. 启动后端SpringBoot应用`douya-oa-parent`

### 启动管理员管理前端

```shell
cd douya-oa-admin
npm install
npm run dev
```

### 启动公众号前端程序

```shell
cd douya-oa-web
npm install
npm run serve
```