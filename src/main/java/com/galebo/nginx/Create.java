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

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Create {
	public static void main(String[] args) throws IOException {
		Logger log = Logger.getLogger(Create.class);
		log.info("start");
		
		Module module = new Module();
		module.setName("concat");
		module.addParameter(new Parameter("enable", false));
		module.addParameter(new Parameter("max_files", 10));
		module.addParameter(new Parameter("unique", true));
		module.addParameter(new Parameter("delimiter", ""));
		module.addParameter(new Parameter("ignore_file_error", false));
		module.addParameter(new Parameter("types", new String[] { "application/x-javascript", "text/css" }));

		FileUtils.writeStringToFile(new File("/WORK/git/tengine/src/http/modules/ngx_http_" + module.getName() + "_module1.c"), genFtlResult("module_handle.ftl", module));

		log.info("end");
	}

	static String genFtlResult(String ftlName, Object inputInfo) {
		try {
			Configuration freemarkerCfg = new Configuration();
			freemarkerCfg.setDirectoryForTemplateLoading(new File("ftl"));
			freemarkerCfg.setEncoding(Locale.getDefault(), "UTF-8");
			Locale.setDefault(Locale.ENGLISH);

			Template template = freemarkerCfg.getTemplate(ftlName);
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
