# simple-flowchain-orchestration
一个自研的轻量级的通用流程链编排工具

## 开始使用
参考：[使用范例](https://github.com/waltertan1988/simple-flowchain-orchestration/blob/main/src/test/java/com/walter/orchestration/test/DemoChainTest.java)
说明：该范例中定义了2条正交业务链【DemoActivityChain】和【DemoPayVipChain】。这2条业务，除了实现各自的独特逻辑外，还需要【有序地】调用另一个公共的可复用的逻辑组件【DemoVipGiftNode】
