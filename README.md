### JDK API文档搜索引擎
#### **项目介绍**：输入查询词,得到若干结果,每一个搜索结果包含三部分:标题,url,摘要
- 索引模块:扫描本地文档,分析内容并构建正排,倒排索引(使用Ansj分词技术)
- 搜索模块:根据查询词,基于倒排索引进行检索,合并文档,设置分词权值,返回检索结果
- 前端模块:编写简单页面,展示搜索结果,点击搜索结果可跳转至对应API文档

**相关技术栈**：正排索引、倒排索引、分词技术、过滤器、HTML、 Servlet、Json、Ajax
