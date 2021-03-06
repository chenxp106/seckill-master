#### 描述功能
* 登录功能
* 图片验证码
* 生成随机秒杀接口
* 使用redis做缓存，和RabbitMQ做消息队列

#### 秒杀的特点
* 秒杀时大量用户会在统一时间进行抢购，网站瞬间访问量剧增
* 秒杀的请求数量远远大于库存数量，只有少部分用户能够秒杀成功
* 业务比较简单，就是下订单减库存

#### 设计理念
* 限流:因为只有少部分用户能秒杀成功，需要限制大部分流量，只允许少部分服务进入后端。
* 削峰：秒杀时有大量用户涌入，在抢购时会有很高的峰值，对服务的压力很大。需要将瞬间的高流量变成一段平缓的流量。使用了消息队列来削峰。
* 内存缓存：秒杀系统最大的瓶颈一般是数据库的读写操作，由于数据库读写属于磁盘IO，性能很低，需要将部分业务逻辑转到内存。使用了redis缓存技术，将热点数据放入缓存中。

#### 具体的方案
##### 前端方案
* 将商品信息的html页面直接存储在redis中，当用户访问商品详情时，直接返回html页面。

##### 后端方案
* 网关层面：通过拦截器对每一个http请求进程预处理。每个请求都判断是否有库存，如果有则放入到请求队列中，否则直接返回客户端拒绝访问。这里库存信息使用使用的内存标记，不必特别精确，因为后序的处理有更详细的判断。
* 服务端：
1. 通过redis缓存部分热点数据，减少对数据库的访问。比如商品信息，用户信息等。
1. 通过消息队列来削峰，将秒杀的请求放入消息队列中，然后将消息队列秒杀请求逐个处理。
* 使用图片验证码，和生成随机秒杀地址来防止用户的恶意秒杀。

#### 超卖
* 采用了消息队列
* 乐观锁：通过带版本号的更新。对所有的请求都有资格去修改，但是会获取该数据的版本号，只有版本号符合的才能更新成功，其他的返回抢购失败。
* 悲观锁：在更新的sql语句带上for update。但是这种锁效率较低，会一直锁住资源

#### 秒杀核心业务
1. 消息队列中监听消息。
1. 获取库存并判断，并且判断是否重复秒杀。
1. 进行秒杀，创建订单。分别将订单表和秒杀订单表写入数据库。
1. 同时将秒杀订单写入到redis中。构成事物，需要加Transaction注解。

#### 数据库设计
总共五张表。用的InnoDB存储引擎，
* goods表：包含商品的基本信息。id，goods_title,detail,goods_image,stock
* seckill_goods:id，goods_id, 秒杀开始时间和结束时间
* seckill_user:秒杀用户表。id, nickname, password,salt等信息
* order_info:订单信息表。id，用户id，商品id，及商品的一些基本信息。
* seckill_order: 秒杀订单表。id，用户id，商品id，订单id

#### 考虑的问题
* 首先是需要秒杀的商品单独列出来。
* 如何处理大并发
* 如何防止接口被刷
* redis 缓存时间的设置
* 是否重复秒杀？ 秒杀时间段？秒杀结束？


#### reids设计
* 使用jedis工具操作redis，配置jedispool
* 封装了redis操作接口。get(前缀，key，类型)
* 对于key的设置，加上了前缀用于区分那种key，防止key重复
* 对于

#### 用户登录逻辑
请求url：/login/do_login
1. 传入用户名和密码，查找数据库判断是否有该用户。然后将密码与salt用过Md5加密。比对数据中的密码是否相等。
1. 如果登录成功，通过UUID生成token。将token作为key，用户seckill_user作为value保存在redis中。
1. 生成Cookie，这里key为字符串"token"，value为生成的token值。这样传入前端的只是token值，保证了安全性。通过response返回给前端
1. 在后面的登录过程中，从缓存中获取用户，避免重复登录。

#### 商品列表
请求url：/goods/to_list
1. 展示所有秒杀的商品信息，包括商品名称，图片，库存，原价与秒杀价等信息
1. 此页面访问量较大，并且除了库存外其他信息基本不变。所有采用将整个页面html保存下来。
1. 先从redis中获取页面信息，如果不存在，则需要手动渲染html。并将html保存到redis中
1. 为了防止库存的信息误差较大，这个页面的的过期时间设置为60秒。意思是60秒之内我们看到的库存是不变的，但是实际上肯定是有变化的。但是库存不是我们的核心业务，库存的误差并不会导致太大的问题。
1. 如果要更加实时显示库存信息，需要将过期时间设置更小一些，但是这样会加大服务器的压力。

#### 商品详情页
请求url: /goods/to_detail{id}
1. 通过商品id查询数据库得到商品的信息。
1. 获取商品秒杀的状态
1. 服务端封装描述商品的数据。返回给客户端。

在详情页中，为了获取更为准确是数据，用户体验更好，需要直接去访问数据库。

#### 获取图片验证码
异步请求url：/miaosha/verifyCode
1. 通过用户名和商品id创建图片验证码。
1. 通过图片缓冲区类，来画图片，包含方框，随机数字的加减乘除。同时将结果放入到redis中，其中redis存储的是key是用户id+商品id，value是图片验证码的结果。
1. 图片验证码用于校验结果，如果点了秒杀，需要校验验证码。
1. 通过ScriptEngine计算值。

#### 获取秒杀随机地址
请求url：/miaosha/path
1. 每次点击秒杀后，都会生成一个随机的秒杀接口。
1. 在生成秒杀接口前，需要判断验证码是否正确。
1. 如果正确，使用UUID生成地址，同时存入到redia中。key为用户id+商品id，value为path。
1. 同时返回path

#### 秒杀请求处理
请求url：/path/do_miaosha
1. 传入参数有user， path， goods_id,
1. 首先判断秒杀地址是否正确。根据user和goods_id，与redis中的path比对。
1. reids中减库存，并且判断是否重复秒杀。
1. 最后放入到消息队列中。（user, goods_id）

