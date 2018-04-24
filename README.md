#Java高并发秒杀系统API

## How to play
1. git clone `https://github.com/codingXiaxw/seckill.git`
2. open IDEA -->  File  -->  New  --> Open 
3. choose seckill's pom.xml，open it
4. update the `jdbc.properties` files about your mysql's username and password
5. deploy the tomcat，and start up
6. enter in the browser: `http://localhost:8080/seckill/list`
7. enjoy it 

## Develop environment
IDEA+Maven+SSM框架。

## 1.相关技术介绍
**MySQL:**
    1.这里我们采用手写代码创建相关表，掌握这种能力对我们以后的项目二次上线会有很大的帮助；
    2.SQL技巧；
    3.事务和行级锁的理解和一些应用。  

**MyBatis:**
    1.DAO层的设计与开发。
    2.MyBatis的合理使用，使用Mapper动态代理的方式进行数据库的访问。
    3.MyBatis和Spring框架的整合:如何高效的去整合MyBatis和Spring框架。  

**Spring:**
    1.Spring IOC帮我们整合Service以及Service所有的依赖。
    2.声明式事务。对Spring声明式事务做一些分析以及它的行为分析。  

**Spring MVC:**
    1.Restful接口设计和使用。Restful现在更多的被应用在一些互联网公司Web层接口的应用上。
    2.框架运作流程。
    3.Spring Controller的使用技巧。  

**前端:**
    1.交互设计。
    2.bootstrap。
    3.JQuery。设计到前端的页面代码我们直接拷贝即可，毕竟真正开发中这样一个项目是由产品经理、前端工程师、后端工程师一起完成的。  

**高并发:**
    1.高并发点和高并发分析。
    2.优化思路并实现。
    
## 2.Java高并发秒杀APi之业务分析与DAO层代码编写

### 2.1用Maven创建我们的项目seckill
在命令行中输入如下命令:  

```
mvn archetype:generate -DgroupId=cn.codingxiaxw.seckill -DartifactId=seckill -Dpackage=cn.codingxiaxw.seckill -Dversion=1.0-SNAPSHOT -DarchetypeArtifactId=maven-archetype-webapp
```

### 2.2秒杀系统业务分析
秒杀系统业务流程如下:  
由图可以发现，整个系统其实是针对库存做的系统。用户成功秒杀商品，
对于我们系统的操作就是:1.减库存。2.记录用户的购买明细。

下面看看我们用户对库存的业务分析:  

记录用户的秒杀成功信息，我们需要记录:
1.谁购买成功了。2.购买成功的时间/有效期。3.付款/发货信息。这些数据组成了用户的秒杀成功信息，也就是用户的购买行为。  

为什么我们的系统需要事务?看如下这些故障:
1.若是用户成功秒杀商品我们记录了其购买明细却没有减库存。导致商品的超卖。
2.减了库存却没有记录用户的购买明细。导致商品的少卖。对于上述两个故障，若是没有事务的支持，损失最大的无疑是我们的用户和商家。
  在MySQL中，它内置的事务机制，可以准确的帮我们完成减库存和记录用户购买明细的过程。  

MySQL实现秒杀的难点分析:当用户A秒杀id为10的商品时，此时MySQL需要进行的操作是:
1.开启事务。
2.更新商品的库存信息。
3.添加用户的购买明细，包括用户秒杀的商品id以及唯一标识用户身份的信息如电话号码等。
4.提交事务。若此时有另一个用户B也在秒杀这件id为10的商品，他就需要等待，等待到用户A成功秒杀到这件商品然后MySQL成功的提交了事务他才能拿到这个id为10的商品的锁从而进行秒杀，而同一时间是不可能只有用户B在等待，肯定是有很多很多的用户都在等待拿到这个行级锁。秒杀的难点就在这里，如何高效的处理这些竞争？如何高效的完成事务?在后面第4个模块如何进行高并发的优化为大家讲解。  

大家看了是不是觉得很复杂?当然不用担心，我们只是实现秒杀的一些功能:
1.秒杀接口的暴露。
2.执行秒杀的操作。
3.相关查询，比如说列表查询，详情页查询。我们实现这三个功能即可。接下来进行具体的编码工作，首先是Dao层的编码。  

### 2.3Dao层设计开发
首先创建数据库，相关表的sql语句我在main包下的sql包中已经给出。