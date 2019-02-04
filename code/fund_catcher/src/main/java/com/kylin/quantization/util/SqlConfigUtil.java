package com.kylin.quantization.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import com.alibaba.fastjson.JSON;

public class SqlConfigUtil {
	private static final String SQL_BASE_PATH = "";
	public static Document SPARK_DOC = null;
	public static Document HIVE_DOC = null;
	private static MapUtil<String, String> mapUtil = new MapUtil<>();
	private static Logger logger = Logger.getLogger(SqlConfigUtil.class);
	static {
		SAXReader reader = new SAXReader();
		InputStream sparkin=null;
		sparkin=SqlConfigUtil.class.getClassLoader().getResourceAsStream(SQL_BASE_PATH + "spark-sql.xml");
		InputStream hivein=null;
		hivein=SqlConfigUtil.class.getClassLoader().getResourceAsStream(SQL_BASE_PATH + "hive-sql.xml");
		try {
			SPARK_DOC = reader.read(sparkin);
			HIVE_DOC = reader.read(hivein);
		} catch (Exception e1) {
			logger.error(e1);
		} finally {
			try {
				sparkin.close();
			} catch (IOException e) {
				logger.error(ExceptionTool.toString(e));
			}finally {
				try {
					hivein.close();
				} catch (IOException e) {
					logger.error(ExceptionTool.toString(e));
				}
			}
		}

	}



	public static String getBaseSql(Document doc, String id) {
		String sql = ((DefaultElement) doc.getRootElement().selectNodes(id).get(0)).getText().replaceAll("\\n", " ")
				.replaceAll("\\t", " ");
		Pattern pattern = Pattern.compile(" +");
		Matcher matcher = pattern.matcher(sql);
		return matcher.replaceAll(" ");
	}

	public static String getBizSql(String id,Document doc) {
		return getBaseSql(doc, id);
	}
	/**
	 * 获取所有带有某种key-value值的id
	 */
	public static List<String> getRuleByKV(String key, String value,Document doc) {
		List<DefaultElement> list = doc.selectNodes("/sqls/*");
		List<String> ids = new ArrayList<>();
		for (DefaultElement d : list) {
			Attribute attr = d.attribute(key);
			if (attr != null) {
				String val = attr.getStringValue();
				if (value.equals(val)) {
					String name = d.getName();
					if (name.startsWith("rule")) {
						ids.add(name.substring(4));
					}

				}
			}
		}
		return ids;
	}

	public static String attr(String tab,String attr,Document doc){
		List<DefaultElement> list = doc.selectNodes("/sqls/" + tab);
		if(list!=null && list.size()!=0){
			Attribute attribute = list.get(0).attribute(attr);
			if(attribute!=null){
				return attribute.getStringValue();
			}
		}

		return null;
	}

	public static void main(String[] args) {
		System.out.println(attr("corr","test",SPARK_DOC));;
	}
	
}





























