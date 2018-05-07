package pers.yang.elastichelper.util;

/**
 * @Author: yang
 * @Date: 2018/5/7.10:54
 * @Desc: to do?
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * thanks for jfinal
 * Prop. Prop can load properties file from CLASSPATH or File object.
 */
public class Prop {
    private static Logger LOG = Logger.getLogger(String.valueOf(Prop.class));
    private Properties properties = null;
    private static String DEFAULT_ENCODING = "UTF-8";

    /**
     * Prop constructor.
     *
     * @see #Prop(String, String)
     */
    public Prop(String fileName) {
        this(fileName, DEFAULT_ENCODING);
    }

    /**
     * Prop constructor
     * <p>
     * Example:<br>
     * Prop prop = new Prop("my_config.txt", "UTF-8");<br>
     * String userName = prop.get("userName");<br><br>
     * <p>
     * prop = new Prop("com/jfinal/file_in_sub_path_of_classpath.txt", "UTF-8");<br>
     * String value = prop.get("key");
     *
     * @param fileName the properties file's name in classpath or the sub directory of classpath
     * @param encoding the encoding
     */
    public Prop(String fileName, String encoding) {
        InputStream inputStream = null;
        try {
            inputStream = getClassLoader().getResourceAsStream(fileName);        // properties.load(Prop.class.getResourceAsStream(fileName));
            if (inputStream == null) {
                throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
            }
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                LOG.info(e.getMessage());
            }
        }
    }

    private ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : getClass().getClassLoader();
    }

    /**
     * Prop constructor.
     *
     * @see #Prop(File, String)
     */
    public Prop(File file) {
        this(file, DEFAULT_ENCODING);
    }

    /**
     * Prop constructor
     * <p>
     * Example:<br>
     * Prop prop = new Prop(new File("/var/config/my_config.txt"), "UTF-8");<br>
     * String userName = prop.get("userName");
     *
     * @param file     the properties File object
     * @param encoding the encoding
     */
    public Prop(File file, String encoding) {
        if (file == null) {
            throw new IllegalArgumentException("File can not be null.");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("File not found : " + file.getName());
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                LOG.info(e.getMessage());
            }
        }
    }

    public Prop append(Prop prop) {
        if (prop == null) {
            throw new IllegalArgumentException("prop can not be null");
        }
        properties.putAll(prop.getProperties());
        return this;
    }

    public Prop append(String fileName, String encoding) {
        return append(new Prop(fileName, encoding));
    }

    public Prop append(String fileName) {
        return append(fileName, DEFAULT_ENCODING);
    }

    public Prop appendIfExists(String fileName, String encoding) {
        try {
            return append(new Prop(fileName, encoding));
        } catch (Exception e) {
            return this;
        }
    }

    public Prop appendIfExists(String fileName) {
        return appendIfExists(fileName, DEFAULT_ENCODING);
    }

    public Prop append(File file, String encoding) {
        return append(new Prop(file, encoding));
    }

    public Prop append(File file) {
        return append(file, DEFAULT_ENCODING);
    }

    public Prop appendIfExists(File file, String encoding) {
        if (file.exists()) {
            append(new Prop(file, encoding));
        }
        return this;
    }

    public Prop appendIfExists(File file) {
        return appendIfExists(file, DEFAULT_ENCODING);
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Long.parseLong(value.trim());
        }
        return defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            value = value.toLowerCase().trim();
            if ("true".equals(value)) {
                return true;
            } else if ("false".equals(value)) {
                return false;
            }
            throw new RuntimeException("The value can not parse to Boolean : " + value);
        }
        return defaultValue;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Properties getProperties() {
        return properties;
    }
}