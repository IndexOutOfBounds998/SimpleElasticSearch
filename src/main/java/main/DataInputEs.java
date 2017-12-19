package main;

/**
 * Created by yang on 2017/7/12.
 */
public class DataInputEs
{
    
    public static void main(String[] args)
    {
        new DataInputThread().start();
        // List list = new ArrayList<String>();
        // list.add("127.0.0.1");
        // IAccessor accessor = new ClientFactoryBuilder.builder().setCLUSTER_NAME("elasticsearch")
        // .setCLIENT_PORT(9300)
        // .setHOSTS(list)
        // .setINIT(true)
        // .create();
    }
    
}
