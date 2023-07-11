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

##### (1) Mybatis-Plus 提供公共字段自动填充
- 在实体类属性上加入`@TableField`注解,指定自动填充策略
- 按照框架要求编写元数据对象处理器,在此类中统一为公共字段赋值,
   此类需要实现 ``MetaObjectHandler``接口
```
 // 更新时填充字段
@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime ;
```
解决: 填充 'createUser' 和'updateUser' 字段
客户端发送一个请求,服务端就有一个线程处理 (一次请求)
**ThreadLocal**
ThreadLocal不是一个Thread,而是Thread的局部变量.当使用ThreadLocal维护变量的时候,ThreadLocal为
每个使用该变量的线程提供独立的变量副本,所以每一个线程都可以独立的改变自己的副本,
而不影响其他线程.**Threadlocal**为每一个线程提供单独一份存储空间,具有线程隔离的
效果,只有在线程内才能获取到对应的值,线程外则不能访问.

ThreadLocal常用的方法
- public void set(value ) 设置当前线程的线程局部变量的值
- public get()            返回当前线程所对应的线程局部变量的值

代码实现 
1. 编写一个BaseContext工具类,基于ThreadLocal封装的工具类
```java
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>(); 
    public static void setCurrentId(Long id){
        threadLocal.set(id) ;
    }
    public static Long getCurrentId(){
        return threadLocal.get() ;
    }
    
}
```
2. 在LoginCheckFilter的 doFilter中调用BaseContext来设置当前登录用户的id
3. 在MyMetaObjectHandler的方法中调用BaseContext获取登录用户的id 

##### (2) 新增分类

需求分析
后台系统中可以管理分类信息,分类包括两种类型,分别是菜品分类和套餐分类.
当我们在后台系统中添加菜品时需要选择一个菜品分类,
当我们在后台系统中添加套餐时需要选择一个套餐分类.

代码开发
- 实体类Category
- mapper接口CategoryMapper
- 业务层接口CategoryService
- 业务层实现类CategoryServiceImpl
- 控制层CategoryController


##### 分类信息分页查询
需求分析
分页展示列表
代码开发
1. 页面发送ajax请求,将分页查询参数(page,pageSize)提交服务器
2. 服务端Controller,接受页面提交的数据并调用Service,查询数据
3. Service调用mapper操作数据库,查询分页数据
4. Controller将查询到的数据响应给页面
5. 页面接收到的分页数据渲染

#### 删除分类
需求分析
在分类管理页面,可以对某个分类进行删除,当分类关联了套餐分类或者菜品分类时不能删除

**代码开发**
1. 页面发送ajax请求,将要删除的菜品id参数提交到服务器
2. 服务端Controller接受页面提交的数据并调用Service删除数据
3. Service调用Mapper操作数据库
> Update Date : 2023/5/10 周三

**功能完善**
1. 实体类Dish和setmeal
2. Mapper接口DishMapper 和SetmealMapper
3. Service接口DishService 和SetmealService
4. Service实现类DishServiceimpl和SetmealServiceimpl

在CategoryService接口增加一个方法 remove(id ) ; 

完成修改菜品分类

> Date: 2023/5/19 周五
#### 菜品管理
目录
- 文件上传下载
- 新增菜品
- 菜品信息分页查询
- 修改菜品

##### 文件上传下载
1. 文件上传
2. 文件下载
3. 文件上传代码
4. 文件下载代码

文件上传时,对页面的form表单要求:
- 采用post请求提交数据
- 采用multipart格式上传
- 使用input的file控件上传

```html
<form method="pos" action="" enctype="multipart/form-data">
    <input name="myFile" type="file">
    <input type="submit" value="提交">
</form>
```
文件上传: 上传到指定文件位置 ,文件名字用UUID生成


文件下载,也称为download,是指将文件从服务器传输到第计算机的过程
通过浏览器进行文件下载
> Date: 2023/5/25 周四

#### 新增菜品

###### 需求分析
后台系统中可以管理菜品信息，通过新增功能来添加一个菜品，在添加
菜品时需要选择当前菜品所属的分类，并且需要上传菜品图片，在移动端
回按照菜品分类来展示对应的菜品信息


数据模型
- dish 菜品表
- dish_flavor 菜品口味表

代码开发
1. 页面（backend/page/food/add.html）发送ajax请求，请求服务端获取**菜品分类**数据并展示到下拉框
2. 页面发送请求图片上传，请求服务端将图片保存到服务器
3. 页面发送请求进行图片下载，回显在页面
4. 点击保存按钮，发送ajax请求，将菜品相关数据以json 的形式提交

导入Dish Dto(Data transfor object 数据传输对象，一般用于展示层与服务层之间的数据传输)用于封装页面提交的数据

> Date : 2023/5/27 周六
#### 菜品的信息分页查询

**菜品分页查询时和服务端交互的过程**
- 页面(backend/page/food/list.html) 发送ajax请求,将分页查询参数(page,pageSize,name)
提交到服务端,获取分页数据
- 页面发送请求,请求服务端进行图片下载,用于图片展示

> Date: 2023/5/30 周三
>
此时页面上展示的少了一列 分类名称,因为后端传给前段的字段中,没有name 属性.

之前写过一个 DishDto (extend Dish) 



#### 修改菜品
- 需求分析
 修改菜品
- 代码开发

1. 页面发送ajax请求,请求服务器获取分类数据(add.html) 和服务端交互
2. 页面发送ajax请求,请求服务端,根据id查询当前菜品的信息,用于信息的回显
3. 页面发送请求,请求服务端进行图片的下载 ,用与图片的回显
4. 点击保存按钮,页面发送ajax请求 ,将修改后的菜品的相关数据以json 的形式提交到服务端


> Date : 2023/6/1 周四

#### 套餐管理业务开发

- **新增套餐**
- **套餐信息分页查询**
- **删除套餐**
    

##### 新增套餐

1. 需求分析 
    
 ##### 代码开发
    - 实体类SetmealDish
    - DTO SetmealDto
    - Mapper接口 SetmealDishService 
    - 业务层接口 
    - 业务层实现类
    - Controller
    
 ##### 具体实现
 1. 页面(add.html)发送ajax请求,请求服务器获取套餐分类的数据并展示到下拉框中
 2. 页面发送ajax请求,请求服务端获取菜品分类数据并展示到菜品窗口 
 3. 页面发送ajax请求,请求服务端,根据菜品分类查询对应的彩屏数据并展示到添加菜品窗口中
 4. 页面发送请求进行图像上传
 5. 页面发送请求进行图像下载
 6. 点击保存按钮,发送ajax请求,将套餐相关数据以json 形式提交到服务端
 

#### 套餐信息分页查询
有序的展示套餐的页面
1. 页面(combo/list.html) 发送ajax请求,将分页查询参数(page,pageSize,name)提交到服务端,获取分页数据
2. 页面发送请求,请求服务端进行图片的下载,回显

删除套餐

#### 手机验证码的登录

1. 在登录页面login 输入手机号,点击[获取验证码] 按钮,页面发送ajax请求,在服务端调用短信
服务API给指定的手机号发送验证码短信
2. 在登录页面输入验证码,点击[登录],发送ajax请求,在服务端处理登录请求


> date 2023/7/11 星期二
 
代码开发
1. 在登录页面(front/page/login.html) 输入手机号，点击[获取验证码]按钮，
页面发送ajax请求，在服务端调用短信服务API给指定的手机号发送验证码短信。
2. 在登录页面输入验证码，点击[登录]，发送ajax请求，在服务端处理登录请求


####　导入用户地址薄
- 需求分析
- 数据模型
- 导入功能代码
- 功能测试　

地址薄，指的是移动端消费者用户的地址信息，用户登录成功之后可以维护自己
的地址信息。用一个用户可以有多个地址信息，但是只能有一个默认地址 

‘用户的’地址信息被存储在 `address_book`中

功能代码
1. 实体类 `AddressBook`
2. Mapper接口　AddressBookMapper
3. 业务层接口　AddressBookService
4. 业务层实现类
5 控制层 AddressBookController

#### 菜品展示

用户登录成功后跳转到系统首页,在首页需要根据分类来展示菜品和套餐.
如果菜品设置了口味信息,需要展示 选择规格按钮比,否则显示按钮.


代码开发
1. 页面(front/index.html) 发送ajax请求,获取 分类数据
2. 页面发送ajax请求,获取第一个分类下的菜品或者套餐
`Note` : 首页加载完成后,还发送了一次ajax请求用于加载购物城数据,
此处可以将这才请求的地址暂修改一下.









 