# 投资顾问后端服务
#### 项目介绍
投资顾问项目主要提供小方app投资顾问的功能，该项目采用前后端分离方式进行开发。
后端服务用于提供理财购买的后端api，接口完全采用Restful的风格，主要功能包括：
- 基金投顾专区展示
- KYC问卷调查
- 根据KYC问卷调查结果进行组合推荐
- 组合详情展示
- 组合签约、追加投资、减少投资、组合解约
- 组合持仓
- 组合调仓查看

#### 技术选型
- 环境：JDK8、maven
- 框架：Springboot + SpringMvc
- 数据库：Oracle
- 缓存：Redis
- RPC：dubbo + zk
- 单元测试：Mockito + PowerMockito
- 统一认证网关

#### 本地构建
- 设置profile为local
- 执行com.foundersc.ifte.invest.adviser.web.InvestmentAdviserWebApplication.main()方法

#### 测试策略
- 单元测试
  - 基于Mock：Mockito+PowerMockito
  - 基于H2：测试dao层
- 集成测试：接口测试
- 功能测试：前后端联调后测试人员进行功能测试

#### 部署
- 发布：基于fcode+cmdb一键式发布
- 负载均衡：
    - 方案一：后端服务注册到统一网关，通过网关进行路由
    - 方案二：nginx负载均衡

#### FAQ
