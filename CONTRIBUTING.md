# Contributing
我们非常欢迎您向Tencent Shadow提交Issue或Pull Request。

# Issue
在Tencent Shadow开源的初期，我们会密切关注所有Issue反馈。晚些时候再根据反馈的情况制定Issue模板。

反馈问题时，请Fork Shadow的代码库到自己的名下。新建分支，添加可以复现问题的最小改动，提交后push到Github上。然后在Issue单中附上你的代码库地址和分支名即可。

```sh
git clone https://github.com/Tencent/Shadow.git //大概之前你已经这样clone过Shadow的代码库了
cd shadow //切换到你clone的shadow目录
git remote add your_name https://github.com/<your_name>/Shadow.git //把你fork的版本库添加成一个远端
git fetch --all //更新所有远端的代码
git checkout -b new_branch_name origin/dev // 基于Shadow代码库的dev分支新建一个分支
//加上你复现问题的修改
git commit
git push -u your_name  //推送new_branch_name分支到你fork的版本库
```
然后你的分支地址应该类似：`https://github.com/<your_name>/Shadow/tree/new_branch_name`

其他人可以用这样的命令获取到你的分支，看到你的提交做了哪些改动，运行并Debug。
```sh
cd shadow //切换到shadow目录
git fetch https://github.com/<your_name>/Shadow.git new_branch_name
git checkout -b new_branch_name FETCH_HEAD
```

# Pull Request
由于PR会修改代码，因此即便是在开源初期，我们也会对PR谨慎处理。

请注意以下问题：

1. 不要提交无意义改动。
1. 除非是提交复现问题的测试用例，请确保`gradlew testSdk`构建成功（需要连接Android设备）
1. 测试机需要至少有API 28，API 19两种机器，以保证ART和Dalvik虚拟机都能正常工作。
1. 尽量原子化的提交，配有较为清晰的提交信息。

我们会根据大家的PR再调整PR的要求的。
