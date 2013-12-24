package com.galebo.nginx;

import java.util.List;

public class Module {
	String name;
	List<Parameter> parameters;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	static public class Parameter{
		TYPE type;
		String direct;
		String name;

		public Parameter(TYPE type, String direct, String name) {
			super();
			this.type = type;
			this.direct = direct;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public TYPE getType() {
			return type;
		}
		public String getDirect() {
			return direct;
		}
		enum TYPE{ngx_array_t,ngx_flag_t ,ngx_uint_t,ngx_str_t,ngx_http_complex_value_t};
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
