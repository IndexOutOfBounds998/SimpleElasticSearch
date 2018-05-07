package test;

import pers.yang.elastichelper.annotations.Document;
import pers.yang.elastichelper.annotations.Field;
import pers.yang.elastichelper.annotations.ID;
import pers.yang.elastichelper.data.FieldType;

/**
 * @Author: yang
 * @Date: 2018/3/8.18:15
 * @Desc: es 建索引实体类 Setting.json.path setting 文件的路径
 * FieldType 字段的类型
 * ID 主键
 * Field 字段名称
 * fieldsName 多字段设置
 * fieldsAnalyzer 多字段分析器 ）
 * settings 配置setting的json 文件
 */


@Document(index = "index", type = "type", replicas = 1, shards = 5, settings = "Setting.json")
public class TestModel {
    @ID
    private Integer post_id;

    @Field(type = FieldType.date)
    private String createtime;
    // 多字段设置 fieldsName多字段名称 多字段的分词器 要和fieldsAnalyzer配对上
    @Field(type = FieldType.keyword, fields = true, fieldsName = {"ik", "pinyin", "first_py"}, fieldsAnalyzer = {"ik_max_word", "full_pinyin_letter_analyzer", "first_py_letter_analyzer"})

    private String post_title;

    @Field(type = FieldType.text)
    private String post_img;

    @Field(type = FieldType.Integer)
    private Integer is_delete;

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

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_img() {
        return post_img;
    }

    public void setPost_img(String post_imgs) {
        this.post_img = post_imgs;
    }
}
