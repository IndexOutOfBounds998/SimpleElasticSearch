# elasticSearchHelper
# 太懒了 懒得写 readme 直接看测试类 就能明白了

引用代码
 pom 
  要用 distributionManagement 方式去下载
   ` <distributionManagement>
         <repository>
             <id>admin</id>
             <url>http://112.74.215.107:8081/repository/maven-releases/</url>
         </repository>
     </distributionManagement>`

#
  `<dependency> 
  <groupId>pers.yang.elastichelper</groupId>
    <artifactId>elasticSearchhelper</artifactId>
    <version>1.0.7</version>
    <packaging>jar</packaging>
    <dependency>`
    
    

功能：
1.根据json生成setting  
2.实体类注解生成mapping 
3.数据的增删改查

查询数据： 
#支持must mustnot should 查询 等bool查询 普通查询 
          
#代码

# IAccessor accessor = new ClientFactoryBuilder.builder().setCLUSTER_NAME("elasticsearch") 
#                .setCLIENT_PORT(9300)
#                .setHOSTS(new ArrayList<>(Arrays.asList("127.0.0.1")))
#                .create(); 
                
然后使用accessor 对象进行增删改查数据     

数据实体类 demo

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
