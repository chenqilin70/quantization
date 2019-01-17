package com.kylin.quantization.util;

import com.kylin.quantization.CatcherMain;
import com.kylin.quantization.computors.BaseSparkMain;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class LoggerBuilder {
    {
        PropertyConfigurator.configure(CatcherMain.class.getClassLoader()
                .getResourceAsStream("log4j_linux.properties"));
    }
    public static Logger build(Class clazz){
        return Logger.getLogger(clazz);
    }
}
