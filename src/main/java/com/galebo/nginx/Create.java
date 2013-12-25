package com.galebo.nginx;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Create {


	static String genFtlResult(Object inputInfo) {
		try {
			Configuration freemarkerCfg = new Configuration();
			freemarkerCfg.setDirectoryForTemplateLoading(new File("ftl"));
			freemarkerCfg.setEncoding(Locale.getDefault(), "UTF-8");
			Locale.setDefault(Locale.ENGLISH);

			Template template = freemarkerCfg.getTemplate("module.ftl");
			template.setEncoding("UTF-8");

			StringWriter writer = new StringWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bean", inputInfo);
			template.process(map, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
