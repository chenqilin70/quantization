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
import org.xml.sax.Attributes;

public class SqlConfigUtil {
	private static final String SQL_BASE_PATH = "";
	private static Document bizDoc = null;
	private static MapUtil<String, String> mapUtil = new MapUtil<>();
	private static Logger logger = Logger.getLogger(SqlConfigUtil.class);
	static {
		SAXReader reader = new SAXReader();
		InputStream bizin=null;
		bizin=SqlConfigUtil.class.getClassLoader().getResourceAsStream(SQL_BASE_PATH + "spark-sql.xml");
		try {
			bizDoc = reader.read(bizin);
		} catch (Exception e1) {
			logger.error(e1);
		} finally {
			try {
				bizin.close();
			} catch (IOException e) {
				logger.error(ExceptionTool.toString(e));
			}
		}

	}

	public static Document getBizDoc() {
		return bizDoc;
	}

	public static String getBaseSql(Document doc, String id) {
		String sql = ((DefaultElement) doc.getRootElement().selectNodes(id).get(0)).getText().replaceAll("\\n", " ")
				.replaceAll("\\t", " ");
		Pattern pattern = Pattern.compile(" +");
		Matcher matcher = pattern.matcher(sql);
		return matcher.replaceAll(" ");
	}

	public static String getBizSql(String id) {
		return getBaseSql(bizDoc, id);
	}
	/**
	 * 获取所有带有某种key-value值的id
	 */
	/*public static List<String> getRuleByKV(String key, String value) {
		List<Node> list = bizDoc.selectNodes("/sqls*//*");
		List<String> ids = new ArrayList<>();
		for (Node d : list) {
			d.get
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
	}*/
	
}
