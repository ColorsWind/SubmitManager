# SubmitManager
轻松收集作业，不再为命名发愁。配合[问卷星](https://www.wjx.cn/)收作业统一输出PDF格式。  
https://www.colors-wind.net/archives/submitmanager.html


# TODO

- [x] 根据表格自动处理文件
- [x] 多线程处理文件
- [x] 自动转换图像文件
- [x] 文件冲突处理
- [x] 自动配置文件权限[^1]
- [x] 添加文件原始信息
- [ ] 处理word文档[^2]



[^1]: 仅支持 Windows 系统

[^2]: 由于word文档牵涉到较多的版权问题，目前还没找到一种可以跨平台的解决方案，请借助 本地软件完成转换 或者求助第三方网站。



# 协议

本软件原创代码部分以AGPL-3.0授权。

UACTool的作者是 [kasp315b](https://github.com/kasp315b/UACTool)， 
其他引用的库可见 [这里](https://github.com/ColorsWind/FileRemap/network/dependencies)。

另外本软件内置了思源黑体，如果你使用其他字体请注意许可。



# 系统需求

- Windows，macOS，Linux
- Java8或更高版本

- 一个问卷星账号

使用Java打开SubmitManager打开jar文件可启动。





# 截图

![](https://cdn.colors-wind.net/usr/uploads/2020/04/3236829459.png)

![](https://cdn.colors-wind.net/usr/uploads/2020/04/2177531772.png)


# 使用方法

## 问卷设置

创建一个表单

![](https://cdn.colors-wind.net/usr/uploads/2020/04/4028696143.png)

收集你需要的信息+文件

![](https://cdn.colors-wind.net/usr/uploads/2020/04/825075670.png)

修改文件类型限制，下图是目前软件支持的文件类型

![](https://cdn.colors-wind.net/usr/uploads/2020/04/3954557715.png)

## 回收表单

下载所需文件，你需要下载附件和答卷数据(按选项序号)

![](https://cdn.colors-wind.net/usr/uploads/2020/04/3491714266.png)

 ## 处理文件

下载的附件解压，浏览选择输入表格和数据数据(解压后的文件夹)的位置，点击转换选项

![](https://cdn.colors-wind.net/usr/uploads/2020/04/3099375763.png)

调整输出文件的文件名，其中{8}表示下载输入数据表格的第八个字段(即H列)，{7}表示表格的第七个字段(即G列)，其他选项按需选择。

![](https://cdn.colors-wind.net/usr/uploads/2020/04/677005836.png)

接着我们只需要点击开始即可完成转换，**注意必须保证转换时没用使用 Excel 打开输入表格**

下图展示了效果

![](https://cdn.colors-wind.net/usr/uploads/2020/04/2938114973.png)

# 进阶使用

## 作业截止日期

![](https://cdn.colors-wind.net/usr/uploads/2020/04/3056114729.png)



## 学生查询作业提交情况

![](https://cdn.colors-wind.net/usr/uploads/2020/04/3734895698.png)



## 尝试合并多个文件

软件是支持同一张问卷收集份文件的，只需要打开转换选项中的尝试合并多个文件，这个功能可以帮助你收集多张图片，软件会按照题目顺序生成PDF。



## 尝试转换图片文件

软件支持图片转换PDF，只许可打开转换选项中的尝试转换图片文件。需要注意的是，转换图片会消耗数倍于图片大小的内存，同时需要一定的时间完成转换。软件支持多线程同时处理图片使这项工作可以在合理的时间内完成。

由于现在智能手机拍摄一张照片大小可能高达数M，但对于作业来说可能不需要这么多的像素，建议调整上传文件大小限制。



## 添加文件原始信息

如果启用该选项，软件会在 PDF 每一页的左上角添加文件转换成 PDF 之前的名称，字体默认 思源黑体，出于控制PDF大小的考虑，软件**不会**把字体文件内嵌到PDF中（如果这么做，PDF的大小会增加9M），所以在没用安装该字体的电脑可能无法显示这个文字。如果需要自定义字体，请将字体文件放到与 SubmitManager.jar 同级目录并命名为 font.ttf。（目前只支持ttf格式的字体文件）



# 如何编译软件

SubmitManager 使用了maven管理依赖。

你需要 maven 来构建这个软件。

```bash
git clone https://github.com/ColorsWind/SubmitManager.git
cd SubmitManager
mvn clean install
```



# 捐助

如果这个软件给你节约了整理文件的时间，请给作者买块面包吧。

https://afdian.net/@ColorsWind
