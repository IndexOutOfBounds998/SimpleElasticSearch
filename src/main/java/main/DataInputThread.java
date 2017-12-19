package main;

import accessor.IAccessor;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import model.ProductsEntity;
import model._MappingKit;
import builder.ClientFactoryBuilder;
import util.DbUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @Author: yang 【youtulu.cn】
 * @Date: 2017/11/22.16:52
 */
public class DataInputThread extends Thread
{
    private static Logger LOG = Logger.getLogger(String.valueOf(DataInputThread.class));
    
    /**
     * @Author: yang
     * @Date: 2017/11/22.16:52
     * @deas 是否是第一次启动
     */
    private boolean isNoFist = false;
    
    @Override
    public void run()
    {
        LOG.info("================es入库定时任务开启==================");
        
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 构造链接属性
        List list = new ArrayList<String>();
        list.add("127.0.0.1");
        IAccessor accessor = new ClientFactoryBuilder.builder().setCLUSTER_NAME("elasticsearch")
            .setCLIENT_PORT(9300)
            .setHOSTS(list)
            .setINIT(true)
            .create();
        
        LOG.info("================初始化数据库==================");
        DbUtil dbUtil = initDb();
        // init();
        LOG.info("================es入库开始=================");
        while (true)
        {
            // 条件
            String where = " WHERE\n" + "\tp.update_time >= (\n" + "\t\tDATE_SUB(NOW(), INTERVAL 10 MINUTE)\n" + "\t)";
            String select = "SELECT\n" + "\tp.product_id,\n" + "\tp.brand_id,\n" + "\tp.user_id,\n"
                + "\tp.classification_id,\n" + "\tp.system_type,\n" + "\tp.product_name,\n" + "p.sell_status,\n"
                + "\tb.brand_name,\n" + "\tb.brand_cnname,\n" + "\tc.classification_name,\n" + "\tp.product_img,\n"
                + "\tp.product_price,\n" + "\tp.is_hide_price,\n" + "\tp.create_time,\n" + "\tp.update_time,\n"
                + "\tp.product_introduction,\n" + "\tp.is_recommend,\n" + "\tp.product_original_price,\n"
                + "\tp.notice,\n" + "\tp.is_delete,\n" + "\tp.product_material,\n" + "\tp.product_code,\n"
                + "\tp.sort\n" + "FROM\n" + "\tproducts AS p\n" + "LEFT JOIN brands AS b ON p.brand_id = b.brand_id\n"
                + "LEFT JOIN classifications AS c ON p.classification_id = c.classification_id";
            // 如果不是第一次的话 直接执行该语句
            if (isNoFist)
            {
                // 查询语句
                select = select + where;
            }
            LOG.info("》》》》》》查询语句为》》》》》》");
            LOG.info(select);
            dbUtil.preparedStatement(select);
            ResultSet resultSet = dbUtil.executeQuery();
            int i = 0;
            try
            {
                List<ProductsEntity> productsEntityList = new ArrayList<ProductsEntity>();
                while (resultSet.next())
                {
                    i++;
                    int pid = resultSet.getInt("product_id");
                    int bid = resultSet.getInt("brand_id");
                    int uid = resultSet.getInt("user_id");
                    int cid = resultSet.getInt("classification_id");
                    int ishideprice = resultSet.getInt("is_hide_price");
                    int isRecommend = resultSet.getInt("is_recommend");
                    int isDelete = resultSet.getInt("is_delete");
                    int sort = resultSet.getInt("sort");
                    String pname = resultSet.getString("product_name");
                    String bname = resultSet.getString("brand_name");
                    String bcname = resultSet.getString("brand_cnname");
                    String cname = resultSet.getString("classification_name");
                    String pimg = resultSet.getString("product_img");
                    String pprice = resultSet.getString("product_price");
                    String createTime = resultSet.getString("create_time");
                    String UpdataTime = resultSet.getString("update_time");
                    String productIntro = resultSet.getString("product_introduction");
                    String productOrgPrice = resultSet.getString("product_original_price");
                    String notice = resultSet.getString("notice");
                    String productMate = resultSet.getString("product_material");
                    String productCode = resultSet.getString("product_code");
                    int system_type = resultSet.getInt("system_type");
                    int sell_status = resultSet.getInt("sell_status");
                    ProductsEntity productEntity = new ProductsEntity();
                    productEntity.setProductId(pid);
                    productEntity.setUserId(uid);
                    productEntity.setBrandName(bname);
                    productEntity.setClassificationName(cname);
                    productEntity.setClassificationId(cid);
                    productEntity.setProductName(pname);
                    productEntity.setBrandCnname(bcname);
                    productEntity.setBrandId(bid);
                    productEntity.setProductImg(pimg);
                    productEntity.setProductPrice(pprice);
                    productEntity.setIsHidePrice(ishideprice);
                    productEntity.setCreateTime(outputFormat.format(inputFormat.parse(createTime.toString())));
                    productEntity.setUpdataTime(outputFormat.format(inputFormat.parse(UpdataTime.toString())));
                    productEntity.setProductIntro(htmlRemoveTag(productIntro));
                    productEntity.setIsRecommend(isRecommend);
                    productEntity.setProductOrgPrice(productOrgPrice);
                    productEntity.setNotice(notice);
                    productEntity.setIsDelete(isDelete);
                    productEntity.setProductMaterial(productMate);
                    productEntity.setProductCode(productCode);
                    productEntity.setSort(sort);
                    productEntity.setSellStatus(sell_status);
                    productEntity.setSystemType(system_type);
                    productsEntityList.add(productEntity);
                }
                if (productsEntityList.size() > 0)
                {
                    accessor.add(productsEntityList);
                }
                
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            // List<Products> list = Products.dao.find(select);
            
            if (!isNoFist)
            {
                LOG.info("初始化一共查询了" + i + "条数据！");
            }
            else
            {
                LOG.info("更新了" + i + "条数据！");
            }
            try
            {
                /**
                 * 十分钟启动一次
                 */
                LOG.info("》》》》休眠10分钟》》》》");
                Thread.sleep(1000 * 60 * 10);
                
                isNoFist = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.info("定时任务发生错误" + e.toString());
            }
        }
        
    }
    
    /**
     * 初始化数据层
     */
    public static void init()
    {
        try
        {
            LOG.info("================读取数据库配置 start==================");
            PropKit.use("a_little_config.txt");
            DruidPlugin dp =
                new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
            
            ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
            _MappingKit.mapping(arp);
            dp.start();
            arp.start();
        }
        catch (Exception e)
        {
            LOG.info(" 链接数据库发生错误" + e.toString());
            e.printStackTrace();
        }
        
        LOG.info("================读取数据库配置 end==================");
    }
    
    public static DbUtil initDb()
    {
        PropKit.use("a_little_config.txt");
        DbUtil dbUtil = new DbUtil(PropKit.get("datasource"), PropKit.get("jdbcUrl"), PropKit.get("user"),
            PropKit.get("password").trim());
        return dbUtil;
    }
    
    /**
     * 删除Html标签
     *
     * @param inputString
     * @return
     */
    public static String htmlRemoveTag(String inputString)
    {
        if (inputString == null)
            return null;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try
        {
            // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            textStr = htmlStr;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return textStr;// 返回文本字符串
    }
}
