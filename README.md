# Reggie
## 后台管理实现

### 功能需求分析



>  Update Date : 2023/5/5

#### 1. 拦截未登录就可以进入index页面
- 使用一个LoginCheckFilter拦截器实现
1. 获取本次请求的URI
2. 判断本次请求是否要处理
3. 如果不需要处理，则直接放行
4. 判断登录状态，如果已经登录，则直接放行
5. 如果没有登录,则显示没有登录结果

#### 2. 新增员工
在后台系统中管理员工信息，点击[添加员工],跳转到添加页面

用户输入信息，点击提交，后台上传到数据库，由于员工`username` 是唯一的，所以如果重复，提示错误。

#### 3. 员工信息分页查询
需求:
- 分页查询员工信息
- 搜索框查询

代码开发
1. 页面发送ajax请求，将分页查询参数(page,pageSize,name)　提交到服务端
2. 服务端Controller接受页面提交的数据并调用Servcice查询数据
3. Service调用Ｍapper操作数据库，查询分页数据
4. Controller将查询到的分页数据相应给页面
5. 页面接收到分页数据并通过ElementUI的Table组件展示到页面上

#### 4. 启用/禁用员工账号
需求分析
- 在员工管理列表页面，可以对某个员工账号进行启动或者禁用操作，账号禁用的员工不能登录
，启用后的员工可以正常登录.
- 只有管理员(admin)可以对其他的用户进行启用，禁用操作，其他用户不显示启用/禁用按钮.

代码开发
1. 页面发送ajax请求，将参数(id ,status) 提交服务器
2. 服务器Controller接受页面的参数，调用Service.
3. Service调用mapper 

问题
- 在修改员工账号状态为<mark> 启动/禁用</mark>时，从request获取得到的当前登录用户的id精度出现损失
解决
> 可以在服务端给页面响应json数据进行处理，将long型统一转换成string 字符串

实现
1. 提供对象转换器JacksonObjectMapper,基于Jackson进行java对象到json数据的转换
2. 在WebMvcConfig配置类中扩展Spring mvc的消息转化器，在此消息转换器中使用提供的对象转换器进行java对象到json数据的转化
```
   /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter() ;
        // 设置对象转换成器
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将上面的消息转换器追加到mvc框架的转换器集合中
        converters.add(0,messageConverter) ; // 优先使用我们的转换器

    }
```

#### 5. 编辑员工信息
需求分析
    在员工管理列表页面点击编辑，跳转到编辑页面，在编辑页面回显员工信息并进行修改，
    最后点击保存

代码开发
1. 点击编辑按钮，页面跳转到add.html ，并在url中携带员工的id(唯一标示)
2. 在add.html页面获取url中的参数[员工id]
3. 发送ajax请求,请求服务端,同时提交员工Id
4. 服务端接收到请求,根据员工id查询员工信息,将员工信息以json形式响应给页面
5. 页面回显员工信息
6. 点击保存按钮,发送ajax请求,将页面中的员工信息以json的形式交给服务端
7. 服务端接受到修改到的员工信息,并进行处理,完成后响应给页面

---
>  Update Date : 2023/5/6 周六 晴
#### 新增菜品分类

- 公共字段自动填充
- 新增分类
- 分类信息分页
- 删除分类
- 修改分类

(1) Mybatis-Plus 提供公共字段自动填充
- 在实体类属性上加入`@TableField`注解,指定自动填充策略
- 按照框架要求编写元数据对象处理器,在此类中统一为公共字段赋值,
   此类需要实现 ``MetaObjectHandler``接口
```
 // 更新时填充字段
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime ;
```

