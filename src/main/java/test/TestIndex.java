package test;

import pers.yang.elastichelper.accessor.IAccessor;
import pers.yang.elastichelper.accessor.IndexAccessor;
import pers.yang.elastichelper.clientmanager.ClientFactoryBuilder;
import pers.yang.elastichelper.clientmanager.IndexHelperBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by yang on 2018/5/6.
 */
public class TestIndex {


    public static void main(String[] args) {

        //创建连接 链接方式1
        IAccessor accessor = new ClientFactoryBuilder
                .builder()
                .setCLUSTER_NAME("elasticsearch")//es别名
                .setCLIENT_PORT(9300)//es 端口
                .setHOSTS(new ArrayList<>(Arrays.asList("127.0.0.1"))) //es 地址
                .create();

        //创建连接 链接方式2
//        IAccessor accessor2 = new ClientFactoryBuilder
//                .builder()
//                .setConfigPath("elasticsearch.properties")
//                .initConfig(true)
//                .create();

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
