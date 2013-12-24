package com.galebo.nginx;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.galebo.nginx.Module.Parameter;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Create {
	public static void main(String[] args) throws IOException {
		Module module=new Module();
		module.setName("concat");
		ArrayList<Parameter> parameters = new ArrayList<Module.Parameter>();
		parameters.add(new Parameter(Module.Parameter.TYPE.ngx_flag_t, "concat", "enable"));
		parameters.add(new Parameter(Module.Parameter.TYPE.ngx_uint_t, "concat_max_files", "max_files"));
		parameters.add(new Parameter(Module.Parameter.TYPE.ngx_flag_t, "concat_unique", "unique"));
		parameters.add(new Parameter(Module.Parameter.TYPE.ngx_str_t, "concat_delimiter", "delimiter"));
		parameters.add(new Parameter(Module.Parameter.TYPE.ngx_flag_t, "concat_ignore_file_error", "ignore_file_error"));
		parameters.add(new Parameter(Module.Parameter.TYPE.ngx_array_t, "concat_types", "types"));
		module.setParameters(parameters);
		FileUtils.writeStringToFile(new File("E:/workspace/git/tengine/src/http/modules/ngx_http_"+module.getName()+"_module1.c"), genFtlResult("module_handle.ftl", module));
	}
	
	static  String genFtlResult(String ftlName, Object inputInfo) {
		try {
			Configuration freemarkerCfg = new Configuration();
			freemarkerCfg.setDirectoryForTemplateLoading(new File("ftl"));
			freemarkerCfg.setEncoding(Locale.getDefault(), "UTF-8");
			Locale.setDefault(Locale.ENGLISH);
	
			Template template = freemarkerCfg.getTemplate(ftlName);
			template.setEncoding("UTF-8");
	
			StringWriter writer = new StringWriter();
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("bean", inputInfo);
			template.process(map, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
