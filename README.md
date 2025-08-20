# 项目使用说明文档

## 1. 项目概述

### 1.1 项目名称

spring-ai

### 1.2 项目组成

项目后端包含两个核心模块：

*   **mcp-server**：为 agent service 提供 mcp 服务

*   **agent service**：核心业务服务模块

### 1.3 模块说明

*   **mcp-server**：目前主要用于练习 mcp 服务的创建，无实际业务用途。若无需该模块，可直接删除

*   **agent service**：核心功能模块，依赖 mcp-server（若保留 mcp-server）

## 2. 环境要求



*   **JDK 版本**：JDK 17 及以上

*   **数据库**：MySQL（版本 5.7 及以上）

*   **网络环境**：需联网访问智普 AI 开发者平台和 trychroma 网站

## 3. 前置准备步骤

### 3.1 数据库初始化



1.  找到项目中 `resource` 目录下的 `mysql.sql` 脚本文件

2.  使用 MySQL 客户端（如 Navicat、MySQL Workbench 等）连接目标数据库

3.  执行 `mysql.sql` 脚本，完成数据库表结构及初始数据的创建

### 3.2 智普 AI API-Key 配置



1.  访问智普 AI 开发者平台（[官方网站](https://www.zhipuai.cn/)）

2.  完成注册并登录账号

3.  在平台控制台中创建应用，获取 `api-key`

4.  打开项目中的 `config.properties` 文件，找到如下配置项并填入获取到的 `api-key`：



```properties
spring.ai.zhipuai.api-key=你的api-key值

```

5. 如需接入openai或者deepseek, 还需配置以下参数
```properties
spring.ai.deepseek.api-key=你的api-key值

#openai
spring.ai.openai.api-key=你的api-key值
spring.ai.openai.chat.model=gpt-4o-mini
```
6. 如果接入了openai, 则需科学上网并配置代理到JVM参数，不然无法访问Open ai的API.
相应jvm参数配置如下, 
```shell
-Dhttp.proxyHost={替换为你的代理地址} -Dhttp.proxyPort={替换为你的代理端口}
-Dhttps.proxyHost={替换为你的代理地址} -Dhttps.proxyPort={替换为你的代理端口}
-Dhttp.nonProxyHosts="localhost|127.0.0.1"
-Dhttps.nonProxyHosts="localhost|127.0.0.1"
```

### 3.3 TryChroma 云数据库配置

1.  访问 TryChroma 网站（[官方网站](https://trychroma.com/)）

2.  注册云账户并登录

3.  在控制台中创建云数据库实例

4.  获取数据库连接所需的 `token` 和 `tenant` 信息

5.  打开项目中的 `config.properties` 文件，找到如下配置项并填入信息：



```properties
spring.ai.vectorstore.chroma.chromaDBToken=你的token值
spring.ai.vectorstore.chroma.tenant=你的tenant值

```
6. 打开AiController.java, 将IS_THE_FIRST_TIME_TO_USE_THIS_APP置为true, 用于首次将文档信息初始化到向量数据库。
## 4. 服务启动步骤

### 4.1 启动顺序说明

必须按照以下顺序启动服务，否则可能导致服务依赖错误：



1.  先启动 `mcp-server`

2.  待 `mcp-server` 完全启动成功后，再启动 `agent service`

### 4.2 启动 mcp-server



1.  进入 `mcp-server` 模块的根目录

2.  执行启动命令：

*   若使用 Maven：`mvn spring-boot:run`

*   若使用打包后的 Jar：`java -jar mcp-server.jar`

1.  观察控制台输出，出现类似 `Started McpServerApplication in x.x seconds` 表示启动成功

### 4.3 启动 agent service



1.  进入 `agent service` 模块的根目录

2.  执行启动命令：

*   若使用 Maven：`mvn spring-boot:run`

*   若使用打包后的 Jar：`java -jar agent-service.jar`

1.  观察控制台输出，出现类似 `Started AgentServiceApplication in x.x seconds` 表示启动成功

## 5. 注意事项



1.  **模块删除说明**：若无需 `mcp-server`，可直接删除该模块目录，但需确保 `agent service` 中已移除对其的依赖引用

2.  **配置文件**：`config.properties` 中的配置项需准确填写，否则可能导致服务启动失败或功能异常

3.  **端口占用**：启动前请确保服务所需端口未被占用（可在各模块的 `application.properties` 中修改端口配置）

4.  **日志查看**：若启动失败，可查看各模块的日志文件（通常在 `logs` 目录下）定位问题

## 6. 常见问题排查



1.  **数据库连接失败**：

*   检查 `mysql.sql` 脚本是否执行成功

*   确认数据库连接配置（用户名、密码、地址等）是否正确

1.  **API-Key 相关错误**：

*   检查智普 AI 的 `api-key` 是否过期

*   确认网络是否能正常访问智普 AI 服务地址

1.  **Chroma 数据库连接失败**：

*   检查 `token` 和 `tenant` 是否正确

*   确认网络是否能正常访问 TryChroma 的云服务地址

1.  **服务启动顺序错误**：

*   若 `agent service` 启动时提示无法连接 mcp 服务，请先停止 `agent service`，启动 `mcp-server` 后再重新启动 `agent service`

