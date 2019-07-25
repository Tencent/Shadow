# Contributing
我们非常欢迎您向Tencent Shadow提交Issue或Pull Request。

# Issue
在Tencent Shadow开源的初期，我们会密切关注所有Issue反馈。晚些时候再根据反馈的情况制定Issue模板。

# Pull Request
由于PR会修改代码，因此即便是在开源初期，我们也会对PR谨慎处理。

请注意以下问题：

1. 不要提交无意义改动。
1. 除非是提交复现问题的测试用例，请确保`gradlew testSdk`构建成功（需要连接Android设备）
1. 测试机需要至少有API 28，API 19两种机器，以保证ART和Dalvik虚拟机都能正常工作。
1. 尽量原子化的提交，配有较为清晰的提交信息。

我们会根据大家的PR再调整PR的要求的。
