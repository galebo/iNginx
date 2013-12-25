package com.galebo.nginx;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.galebo.nginx.Module.Parameter;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author galebo
 * 
 * Copyright (C) 2013 galebo E-mail(galebo@163.com) QQ(9747375)
 */


public class Create {


	public String genFtlResult(Object inputInfo) {
		try {
			Configuration freemarkerCfg = new Configuration();
			freemarkerCfg.setEncoding(Locale.getDefault(), "UTF-8");
			Locale.setDefault(Locale.ENGLISH);
			freemarkerCfg.setTemplateLoader(new ClassTemplateLoader(Create.class, "/ftl"));
			Template template = freemarkerCfg.getTemplate("module.ftl");
			template.setEncoding("UTF-8");

			StringWriter writer = new StringWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bean", inputInfo);
			template.process(map, writer);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	public static void main(String[] args) throws IOException {
		Logger log = Logger.getLogger(Create.class);
		log.info("start");
		
		Module module = new Module("concat",false);
		module.addParameter(new Parameter("enable", false));
		module.addParameter(new Parameter("max_files", 10));
		module.addParameter(new Parameter("unique", true));
		module.addParameter(new Parameter("delimiter", ""));
		module.addParameter(new Parameter("ignore_file_error", false));
		module.addParameter(new Parameter("types", new String[] { "application/x-javascript", "text/css" }));

		FileUtils.writeStringToFile(new File("/WORK/git/tengine/src/http/modules/ngx_http_" + module.getName() + "_module1.c"),
				new Create().genFtlResult(module));

		log.info("end");
	}
}
