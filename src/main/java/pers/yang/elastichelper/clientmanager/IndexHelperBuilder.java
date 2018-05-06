package pers.yang.elastichelper.clientmanager;

import org.elasticsearch.client.transport.TransportClient;
import pers.yang.elastichelper.accessor.IndexAccessor;
import pers.yang.elastichelper.accessor.IndexAccessorImpl;

/**
 * Created by yang on 2018/5/6.
 * <p>
 * 构建index mapping setting
 */
public class IndexHelperBuilder {

    private static TransportClient client;

    public static class Builder {
        private IndexHelperBuilder builder;


        public Builder withClient(TransportClient client) {
            IndexHelperBuilder.client = client;
            return this;
        }


        public IndexAccessor creatAccessor() {
            return new IndexAccessorImpl(client);
        }

    }
}
