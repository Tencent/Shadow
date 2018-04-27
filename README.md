# Android Library项目模板

提供一个在公司内开发、维护Android Library,并且将它发布到maven.oa.com的一个模版。

# 推荐给其他团队使用前的TODO LIST
- publish任务自动依赖assemble任务
- 发布脚本独立成gradle plugin,实现配置化

# 推荐规范

- 不要使用类似如下形式引用一个目录中的所有jar包
```
implementation fileTree(dir: 'libs', include: ['*.jar'])
```