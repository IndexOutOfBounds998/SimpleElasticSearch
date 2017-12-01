package config;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;


public class _JFinalDemoGenerator {
	
	public static DataSource getDataSource() {
		PropKit.use("a_little_config.txt");
		DruidPlugin druidPlugin = MainConfig.createDruidPlugin();
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		String baseModelPackageName = "model.base";
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/model/base";
		String modelPackageName = "model";
		String modelOutputDir = PathKit.getWebRootPath() + "/src/main/java/model";
		Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		generator.setGenerateChainSetter(false);
		generator.addExcludedTable("api_user","brands","products_copy");
		generator.setGenerateDaoInModel(true);
		generator.setGenerateChainSetter(true);
		generator.setGenerateDataDictionary(false);
		generator.generate();
	}
}




