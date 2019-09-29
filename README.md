## 调试

IDE 设置一个 remote debug的run 配置

命令行启动编译，满足

* 开启debug
* 关闭守护进程

命令：
```shell
./gradlew clean :app:assembleDebug -Dorg.gradle.debug=true --no-daemon
``` 