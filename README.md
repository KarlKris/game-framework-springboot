# game-framework-springboot
基于SpringBoot2.3的游戏服务器框架

从事Java开发的游戏服务端开发,尝试自己写一套MMO的多进程服务器架构

模块划分:
game-battle     :基于行为树的MMO战斗逻辑(30%)
game-cluster    :基于ZooKeeper的服务注册和发现
game-common     :基础包
game-core       :业务逻辑核心包(缓存,事件发布与处理,数据持久化)
game-engine     :基于Netty的服务端
game-network    :基于Netty的网络基础包
game-protocol   :网络协议包

game-client     :基于Netty的客户端
game-manager    :基于Spring-Reactive-Web的后台(10%)
game-gateway    :网关服
game-server     :游戏服   

技术栈:
SpringBoot2,Netty,ZooKeeper,Redis,Disruptor,Javassist,Hibernate5,ProtoStuff
