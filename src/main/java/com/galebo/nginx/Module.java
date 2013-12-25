package com.galebo.nginx;

import java.util.ArrayList;
import java.util.List;

import com.galebo.nginx.Module.Parameter.TYPE;

public class Module {
	String name;
	int parameterMaxLength=1;
	private boolean isFilter=false;
	ArrayList<Parameter> parameters = new ArrayList<Module.Parameter>();
	List<Parameter> arrayParameters=new ArrayList<Module.Parameter>();
	
	public String getName() {
		return name;
	}
	public  Module(String name,boolean isFilter) {
		this.name = name;
		this.isFilter=isFilter;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public int getParameterMaxLength() {
		return parameterMaxLength;
	}
	public boolean isFilter() {
		return isFilter;
	}
	
	
	public void addParameter(Parameter parameter) {
		if(parameter.direct==null){
			if(parameter.name.equals("enable"))
				parameter.direct = name;
			else
				parameter.direct=name+"_"+parameter.name;
		}
		parameters.add(parameter);
		if(parameter.type==TYPE.ngx_array_t)
		{
			arrayParameters.add(parameter);
		}
		if(parameterMaxLength<parameter.name.length()){
			parameterMaxLength=parameter.name.length()+1;
		}
	}
	public List<Parameter> getArrayParameters() {
		return arrayParameters;
	}
	static public class Parameter{
		private TYPE type;
		private String direct;
		private String name;
		private Object defaultValue;
		public Parameter(String direct, String name,Object defaultValue) {
			this(name,defaultValue);
			this.direct = direct;
		}
		public Parameter(String name,Object defaultValue) {
			super();
			this.name = name;
			this.defaultValue=defaultValue;
			if(defaultValue instanceof Integer)
				this.type=TYPE.ngx_uint_t;
			if(defaultValue instanceof String)
				this.type=TYPE.ngx_str_t;
			if(defaultValue instanceof Boolean)
				this.type=TYPE.ngx_flag_t;
			if(defaultValue instanceof String[])
				this.type=TYPE.ngx_array_t;
		}

		public Object getDefaultValue2() {
			return defaultValue;
		}
		public String getDefaultValue() {
			if(defaultValue instanceof Integer)
				return defaultValue.toString();
			if(defaultValue instanceof String)
				return defaultValue.toString();
			if(defaultValue instanceof Boolean){
				Boolean _defaultValue=(Boolean)defaultValue;
				return (_defaultValue)?"1":"0";
			}
			if(defaultValue instanceof String[])
				return "bad call";
			return "bad value";
		}
		public String getName() {
			return name;
		}
		public String getDirect() {
			return direct;
		}
		enum TYPE{ngx_array_t,ngx_flag_t,ngx_uint_t,ngx_str_t,ngx_http_complex_value_t};
		public String getTypeName(){
			if(type==TYPE.ngx_array_t				) return "ngx_array_t";
			if(type==TYPE.ngx_flag_t 				) return "ngx_flag_t";
			if(type==TYPE.ngx_uint_t				) return "ngx_uint_t";
			if(type==TYPE.ngx_str_t					) return "ngx_str_t";
			if(type==TYPE.ngx_http_complex_value_t	) return "ngx_http_complex_value_t";
			return "";
		}
		public String getSlot(){
			if(type==TYPE.ngx_array_t				) return "ngx_http_types_slot";
			if(type==TYPE.ngx_flag_t 				) return "ngx_conf_set_flag_slot";
			if(type==TYPE.ngx_uint_t				) return "ngx_conf_set_num_slot";
			if(type==TYPE.ngx_str_t					) return "ngx_conf_set_str_slot";
			if(type==TYPE.ngx_http_complex_value_t	) return "ngx_http_complex_value_t";
			return "";
		}
	}
}
