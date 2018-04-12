# elasticSearch
功能：
1.生成json生成setting  
2.实体类注解生成mapping 
3.数据的增删改查

查询数据： 
#支持must mustnot should 查询 等bool查询 普通查询 
          
#使用方式：

#IAccessor accessor = new ClientFactoryBuilder.builder().setCLUSTER_NAME("elasticsearch") 
#                .setCLIENT_PORT(9300)
#                .setHOSTS(new ArrayList<>(Arrays.asList("127.0.0.1")))
#                .create(); 
                
然后使用accessor 对象进行增删改查数据     
