iNginx
======
##Introduction
this project is helpful for quickly creating one basically nginx module file in your nginx project.you can use this file to begin your nginx coding.

##how to use

1.check out this project.
it is a java&maven project,so the best method is using Eclipse,and if you say not install then the next step you know ...;

2.edit Create.java file (in src\main\java\com\galebo\nginx\)

for example : below is concat module configuration

		Module module = new Module("concat",false);         //first parameter is module name ,
		                                                    //second parameter is filter switch (false means handle,true mean a filter module)
		
		//add parameter,first name ,second default value;
		//and default value's type mean this parameter's type,so you can use boolean,string,integer,and string array
		module.addParameter(new Parameter("enable", false));           //module switch ,name must be enable;
		module.addParameter(new Parameter("max_files", 10));           //integer
		module.addParameter(new Parameter("unique", true));            //boolean
		module.addParameter(new Parameter("delimiter", ""));           //string
		module.addParameter(new Parameter("ignore_file_error", false));//boolean
		module.addParameter(new Parameter("types", new String[] { "application/x-javascript", "text/css" }));//string array

3.edit file path

	FileUtils.writeStringToFile(new File("/WORK/git/tengine/src/http/modules/ngx_http_" + module.getName() + "_module1.c"),
				new Create().genFtlResult(module));
				
4.run java application

5.the output file is as "src\test\resources\TestCase\module.c"
	[`module.c`](https://github.com/galebo/iNginx/blob/master/src/test/resources/TestCase/module.c)