package com.galebo.nginx;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.galebo.nginx.Module.Parameter;

public class TestCreate extends TestCase{
	Create Create=new Create();
	public void testHandle() throws IOException {
		Logger log = Logger.getLogger(Create.class);
		log.info("start");
		
		Module module = new Module("concat",false);
		module.addParameter(new Parameter("enable", false));
		module.addParameter(new Parameter("max_files", 10));
		module.addParameter(new Parameter("unique", true));
		module.addParameter(new Parameter("delimiter", ""));
		module.addParameter(new Parameter("ignore_file_error", false));
		module.addParameter(new Parameter("types", new String[] { "application/x-javascript", "text/css" }));

		assertEqualsFile(Create.genFtlResult(module),"/TestCase/module.c");

		log.info("end");
	}
	void assertEqualsFile(String actual,String fileName) throws IOException
	{
		String expected=null;

		URL resource = this.getClass().getResource(fileName);
		
		expected = FileUtils.readFileToString(new File(resource.getFile()));
		System.out.println(actual);
	
		assertEquals(expected, actual.replaceAll("\r\n", "\n"));
	}
}
