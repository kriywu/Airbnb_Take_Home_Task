# README 爱彼迎实习take home work

### 开发&测试环境

- Windows 10 Java 8
- Android Studio 3.2
- 华为荣耀8 Android 8.0 



### 类介绍

- Abstract 抽象类或者接口
   - BindHolder : FootHolder 和 ItemHolder 的父类，主要是将bind()方法抽象出来，减少onBindViewHolder中的逻辑。
   - ResultCallback : 联网请求回调，onSuccess() 和 onFailed()

- Holder RecyclerView.ViewHolder内容
  - FootHolder : 用于RecyclerView上拉显示更多时候最后一个元素，显示"Loading"的时候正在加载。显示"Hit Bottom"表示该用户所有的内容都加载完成
  - ItemHolder : 用于显示每个项目的信息，包括Avatar头像，项目名称，以及Star数量
- Module 模型层
  - Project : 项目类，字段：项目名称（name），点赞数量（star），头像URL（avatar）
- Util 帮助类
  - CacheDbCRUD：封装数据库操作，数据库缓存搜索记录，数据库内容的生存周期为应用的生存周期，当应用退出的时候，数据库将被清空。每次联网的数据都会缓存到数据库。当下拉RecyclerView刷新的时候，不会去读数据库中的内容，会直接去联网读取数据，再刷新数据库，相当于更新缓存。
  - Constants：存放一些常量，URL，数据库名称，表名称
  - HttpStringUtil：根据name和page生成URL，解析返回的JSON数据
  - MyDatabaseHelper：建立数据库
  - OkHttpEngine：封装联网存，并将回调的处理切换到UI线程，目的在于更新UI
- MainActivity ： 主要的界面，包括SearchView，RecyclerView。



### 功能完成情况

| 功能                                               | 完成情况 |
| -------------------------------------------------- | -------- |
| 根据API查询，并显示到列表中                        | 完成     |
| 分页加载                                           | 完成     |
| 没有网络时候，显示提示语                           | 完成     |
| 没有查询到内容，显示提示语                         | 完成     |
| 查询结果缓存（缓存的生命周期和应用的生命周期一致） | 完成     |



### 应用逻辑结构

1. 打开应用，显示MainActivity，此时RecyclerView内容为空

2. 在SearchView中输入name，提交查询

   1. 先检查数据库是否有缓存，如果有缓存，直接返回缓存

   2. 如果数据库没有缓存，联网查询

   3. 如果查询结果不为null，显示列表，并将结果存入缓存数据库

   4. 如果网络状态有问题，或者结果为空，显示对应的提示语

3. 下拉刷新

   1.  直接访问网络（这里不会去读取缓存数据库）

   2. 对结果的处理逻辑和【2】类似，区别的是，将数据库中指定name的缓存清楚，再把新的数据存入到数据库。

4. 上拉加载更多

   1. 先检查数据库是否有缓存，如果有缓存，直接读取数据库内容
   2. 如果数据库没有对应的缓存，联网查询
   3. 如果返回的JSON是"[]"表示没有指定page内容，再FootHolder中显示”Hit Bottom“
   4. 如果返回的JSON不是"[]"，表示有新的内容，加入列表，并且插入缓存数据库。

---

注：分页的页大小最大为30，说明当JSON中数据等于30的时候，表示可能存在page+1页内容。如果当前page返回的数据小于30条，表面已经加载完毕。显示”Hit Bottom即可“





### 第三方库使用情况

| 名称   | 用途         |
| ------ | ------------ |
| okhttp | 联网请求数据 |
| glide  | 加载照片     |













​	



