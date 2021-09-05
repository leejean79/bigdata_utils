package com.atguigu.practice.meta;

import com.atguigu.practice.model.ErrorCode;
import com.atguigu.practice.model.ServiceException;
import com.atguigu.practice.model.TopicConfig;
import com.atguigu.practice.utils.Utils;
import org.boon.json.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
通过内部静态类创建配置文件的单例模式，保证多线程中的唯一性
*/
public class ConfManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfManager.class);

    public static ServerConfig serverConfig;

    /*
    创建一个map，key：配置文件名，value：配置文件的对象
    */
    public static Map<String, TopicConfig> topicConfigMap;

    static {
        //加载系统配置文件，如：codis的ip地址等
        serverConfig = new ServerConfig(Utils.loadPropsByCL("client.properties", ConfManager.class));
    }

    /*
    私有化构造函数
    */
    private ConfManager() {
    }

    /*
    定义私有静态内部类，返回外部类的实例
    */
    private static class SingletonHelper {
        private static final ConfManager INSTANCE = new ConfManager();
    }

    /*
    单例模式
    */
    public static ConfManager getInstance() {
        return SingletonHelper. INSTANCE;
    }

    /*
    封装对象的方法
    */
    private void load(String name, String configJson) throws ServiceException {

        org.boon.json.ObjectMapper mapper = JsonFactory.create();
        TopicConfig config = mapper.fromJson(configJson, TopicConfig.class);

        if(config != null ){
            if(topicConfigMap == null) {
                topicConfigMap = new HashMap<>();
            }
            topicConfigMap.put(name, config);
        } else {
            throw new ServiceException(ErrorCode.ERRORCODE_LOAD_TOPIC_CONFIG_FAIL);
        }

    }

    /*
    转换的主要过程
    */
    private void initFromFile() throws IOException {

//         1. 读取工程根路径resource下的conf文件夹下的配置文件名
        InputStream dirInputStream = ConfManager.class.getClassLoader().getResourceAsStream("conf");

        BufferedReader dirReader = new BufferedReader(new InputStreamReader(dirInputStream));
        String configFileName;
//          2. 如果存在配置文件，则通过正则提取文件
        while ((configFileName = dirReader.readLine()) != null) {
            LOGGER.error("ConfigFileNam=" + configFileName);
            Pattern configFilePattern = Pattern.compile("event\\-\\w+\\.json");
            Matcher configFileMatcher = configFilePattern.matcher(configFileName);
            if (configFileMatcher.find()) {
//                 3. 读取配置文件的内容
                InputStream configFileStream = ConfManager.class.getClassLoader()
                        .getResourceAsStream("conf/" + configFileName);
                BufferedReader configReader = new BufferedReader(new InputStreamReader(configFileStream));
                StringBuffer configJson = new StringBuffer();
                String configLineInfo;
                while ((configLineInfo = configReader.readLine()) != null) {
                    configJson.append(configLineInfo);
                }
                try {
//                  4. 将该json文件转为对应的对象
                    load(configFileName.substring(0, configFileName.indexOf(".")), configJson.toString());

                } catch (ServiceException e) {
                    LOGGER.error("msg:[MDERROR load error], filename:" + configFileName, e);
                }
                configReader.close();
                configFileStream.close();
            }
        }

        dirReader.close();
        dirInputStream.close();
    }


    public synchronized void init() {
        try {
            initFromFile();
        } catch (IOException e){
            LOGGER.error(ErrorCode.ERRORCODE_LOAD_TOPIC_CONFIG_FAIL.getMsg(), e);
        }
    }

    public int getNumThreads(String topic) {
        if(topicConfigMap == null || topicConfigMap.isEmpty()) {
            return 0;
        }

        TopicConfig topicConfig = topicConfigMap.get(topic);
        if(topicConfig == null) {
            return 0;
        }
        return topicConfig.getPartitions();
    }

//    public static void close() {
//        for(Map.Entry<Integer, Jedis> entry : localJedisMap.entrySet()) {
//            Jedis jedis = entry.getValue();
//
//            if(jedis != null) {
//                jedis.close();
//                LOGGER.error("close local jedis before shutdown " + entry.getKey());
//            }
//
//        }
//        localJedisPool.close();
//    }

}
