# elasticSearchHelper
#  thanks for es
# 着手进行第二版本的开发ing 更少的代码 更简洁的方式 let's go!

引用代码
 pom 
  #要用 distributionManagement 方式去下载
 
```java 
   <distributionManagement>
         <repository>
             <id>admin</id>
             <url>http://112.74.215.107:8081/repository/maven-releases/</url>
         </repository>
     </distributionManagement>


  <dependency> 
  <groupId>pers.yang.elastichelper</groupId>
    <artifactId>elasticSearchhelper</artifactId>
    <version>1.0.7</version>
    <dependency>
    
```
    

功能：
#1.根据json生成setting  
#2.实体类注解生成mapping 
#3.数据的增删改查

查询数据： 
#支持must mustnot should 查询 等bool查询 普通查询 
      
#用链接2的方式 需要配置配置文件        
```properties
         #elasticsearch集群名称
         cluster.name = elasticsearch
         #elasticsearch集群中的主机地址列表（多个主机，以","分隔）
         #hosts = 192.168.3.41
         hosts=127.0.0.1
         #elasticsearch客户端连接端口号（默认9300）
         client.port = 9300
         #默认查询时显示的开始位置
         start = 0
         #默认查询时显示的每页记录数量
         rows = 10


```
          
**1. 创建连接 创建setting mapping index**

```java
 //创建连接 链接方式1 然后使用accessor 对象进行增删改查数据 
 public class TestIndex {
      public static void main(String[] args) {
        IAccessor accessor = new ClientFactoryBuilder
                .Builder()
                .setCLUSTER_NAME("elasticsearch")//es别名
                .setCLIENT_PORT(9300)//es 端口
                .setHOSTS(new ArrayList<>(Arrays.asList("127.0.0.1"))) //es 地址
                .create();

        //创建连接 链接方式2
        IAccessor accessor2 = new ClientFactoryBuilder
                .Config()
                .setConfigPath("elasticsearch.properties")
                .initConfig(true)
                .createByConfig();

        //构建indexhelper
        IndexAccessor indexAccessor = new IndexHelperBuilder
                .Builder()
                .withClient(accessor.getClient())
                .creatAccessor();

        //创建索引 mapping 和setting
        //通过model 生成index mapping  和setting
        boolean flag = indexAccessor.createIndexWithSettings(TestModel.class);
        //添加一条数据
        TestModel model = new TestModel();
        model.setPost_id(1);
        model.setCreatetime(new Date().toString());
        model.setIs_delete(1);
        model.setPost_title("测试啊");
        boolean flagAdd = accessor.add(model);


        //添加多条数据、
        List<TestModel> testModelList = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            TestModel model2 = new TestModel();
            model2.setPost_id(i); //主键不要重复 重复的做updata操作
            model2.setCreatetime(new Date().toString());
            model2.setIs_delete(1);
            model2.setPost_title("测试啊");
            testModelList.add(model2);
        }
        boolean flagAddList = accessor.add(testModelList);
        //删除一条 id 为1的数据
        accessor.delete("1", TestModel.class);
        //详情见IAccessor 接口
        }
        }
   
``` 
   **2. es搜索**
   ```java
       public class TestSearch {
     public static void main(String[] args) {
          BoolQueryBuilder rootBuilder = QueryBuilders.boolQuery();
                //搜索商品名称 名称进行ik分词+商品sku
                Map<String, Float> fieldsMap = new HashMap<>();
                fieldsMap.put("product_name.ik", (float) 2);
                fieldsMap.put("product_code", (float) 1);
                QueryStringQueryBuilder fieldsMapBuilder = new QueryStringQueryBuilder(keyWord);
                fieldsMapBuilder.fields(fieldsMap);
                //创建过滤构造builder
                BoolQueryBuilder filterbuilder = QueryBuilders.boolQuery();
                //添加过滤参数 未删除的已经上架的商品
                filterbuilder.must(QueryBuilders.termQuery("is_delete", 0));
                filterbuilder.must(QueryBuilders.termQuery("seller_status", 1));
                //构造总体查询
                rootBuilder.filter(filterbuilder);
                rootBuilder.should(fieldsMapBuilder);
                //创建QueryBuilderCondition 构建整体的查询
                QueryBuilderCondition booleanCondtionBuilder = new QueryBuilderCondition.builder()
                        .setBoolQueryBuilder(rootBuilder)
                        .setStart(pageSize * (pageIndex - 1))
                        .setRow(pageSize)
                        .setMinScore(0)
                        .builder();
                Result result = null;
                try {
                    //开始搜索 just for fun
                    result = EleasticAccessorManager
                            .ACCESSOR_MANAGE
                            .getAccessor()
                            .searchFun(ProductsEntity.class, booleanCondtionBuilder);
                } catch (Exception e) {
                    logger.error("elasticSearch 查询接口异常 异常信息：" + e.getMessage());
                }   
                //查询的数据list
           result.getList();
       result.getSearchHits().getTotalHits()
       }
       }
``` 


  **3. 数据实体类**
```java
import annotations.Document;
import annotations.Field;
import annotations.ID;
import data.FieldType;

/**
 * @Author: yang
 * @Date: 2018/3/8.18:15
 * @Desc: es 建索引实体类
 */
@Document(index = "index", type = "type", replicas = 1, shards = 5, settings = "Setting.json")
public class ProductsEntity {
    @ID
    private Integer post_id;

    @Field(type = FieldType.date)
    private String createtime;

    @Field(type = FieldType.date)
    private String updatatime;

    @Field(type = FieldType.keyword, fields = true, fieldsName = {"ik", "pinyin", "first_py"}, fieldsAnalyzer = {"ik_max_word", "full_pinyin_letter_analyzer", "first_py_letter_analyzer"})

    private String post_title;

    @Field(type = FieldType.keyword,  fields = true, fieldsName = {"ik", "pinyin", "first_py"}, fieldsAnalyzer = {"ik_max_word", "full_pinyin_letter_analyzer", "first_py_letter_analyzer"})
    private String post_content;

    @Field(type = FieldType.keyword, fields = true, fieldsName = {"ik", "pinyin", "first_py"}, fieldsAnalyzer = {"ik_max_word", "full_pinyin_letter_analyzer", "first_py_letter_analyzer"})
    private String btt_name;

    @Field(type = FieldType.text)
    private String post_img;

    @Field(type = FieldType.Integer)
    private Integer comments_amount;

    @Field(type = FieldType.Integer)
    private Integer like_amount;

    @Field(type = FieldType.Integer)
    private Integer green_status;

    @Field(type = FieldType.Integer)
    private Integer is_delete;

    @Field(type = FieldType.Integer)
    private Integer user_id;

    @Field(type = FieldType.text)
    private String user_avatar;

    @Field(type = FieldType.keyword,  fields = true, fieldsName = {"ik", "pinyin", "first_py"}, fieldsAnalyzer = {"ik_max_word", "full_pinyin_letter_analyzer", "first_py_letter_analyzer"})
    private String nick_name;


    public String getBtt_name() {
        return btt_name;
    }

    public void setBtt_name(String btt_name) {
        this.btt_name = btt_name;
    }

    public Integer getGreen_status() {
        return green_status;
    }

    public void setGreen_status(Integer green_status) {
        this.green_status = green_status;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatatime() {
        return updatatime;
    }

    public void setUpdatatime(String updatatime) {
        this.updatatime = updatatime;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_img() {
        return post_img;
    }

    public void setPost_img(String post_imgs) {
        this.post_img = post_imgs;
    }

    public Integer getComments_amount() {
        return comments_amount;
    }

    public void setComments_amount(Integer comments_amount) {
        this.comments_amount = comments_amount;
    }

    public Integer getLike_amount() {
        return like_amount;
    }

    public void setLike_amount(Integer like_amount) {
        this.like_amount = like_amount;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }


    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}

```
