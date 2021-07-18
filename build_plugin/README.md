# 使用Composing builds 管理依赖

## Start

* Step-1. 复制build_plugin模块到项目根目录下（与需要管理依赖的模块同级）
* Step-2. 在根项目的`settings.gradle`中将此模块添加为构建模块`includeBuild 'build_plugin'`
* Step-3. 在应用级别的子模块中引入plugin：  
  ```groovy
  // file: app/build.gradle
  plugins {
      id 'xyz.dean.build-plugin'
      // ...
  }
  ```

## 相关资料
[再见吧 buildSrc, 拥抱 Composing builds 提升 Android 编译速度](https://juejin.cn/post/6844904176250519565)