<#macro addSpace len>
	<#compress>
		<#list 1..len as one> </#list>
	</#compress>
</#macro>

<#macro print str width>
	<#compress>
		${str}<#list 1..(width-str?length) as one> </#list>
	</#compress>
</#macro>


<#macro getTake parameter>
	<#compress>
	<#if     parameter.typeName = 'ngx_flag_t' >NGX_CONF_FLAG
	<#elseif parameter.typeName = 'ngx_str_t'  >NGX_CONF_TAKE1
	<#elseif parameter.typeName = 'ngx_uint_t' >NGX_CONF_TAKE1
	<#elseif parameter.typeName = 'ngx_array_t'>NGX_CONF_1MORE
	</#if>
	</#compress>
</#macro>


<#macro define module filter="_filter">
<#assign len1=10-module.name?length-filter?length/>
<#assign len=10-module.name?length/>
<#assign pLen=20/>

#include <ngx_config.h>
#include <ngx_core.h>
#include <ngx_http.h>


typedef struct {
    <#lt><#list module.parameters as one>
		<#lt><#if one.typeName = 'ngx_array_t'>
	
    ngx_hash_t   ${one.name};
    ngx_array_t *${one.name}_keys;
		<#lt><#else>    <@print str=one.typeName width=13 />${one.name};
		<#lt></#if>
	<#lt></#list>
} ngx_http_${module.name}_loc_conf_t;

<#list module.arrayParameters as one>
static ngx_str_t  ngx_http_${module.name}_default_${one.name}[] = {
<#list one.defaultValue2 as one1>
    ngx_string("${one1}"),
</#list>
    ngx_null_string
};
</#list>


static void *ngx_http_${module.name}_create_loc_conf(ngx_conf_t *cf);
static char *ngx_http_${module.name}_merge_loc_conf(ngx_conf_t *cf,void *parent, void *child);
static ngx_int_t ngx_http_${module.name}${filter}_init(ngx_conf_t *cf);


static ngx_command_t  ngx_http_${module.name}${filter}_commands[] = {

    <#list module.parameters as one>
    { ngx_string("${one.direct}"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|<@getTake parameter=one/>,
      ${one.slot},
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, ${one.name}<#if one.typeName = 'ngx_array_t'>_keys</#if>),
      <#if one.typeName = 'ngx_array_t'>&ngx_http_${module.name}_default_${one.name}[0]<#else>NULL</#if> },
      
	</#list>
      ngx_null_command
};


static ngx_http_module_t  ngx_http_${module.name}${filter}_module_ctx = {
    NULL,                                 <@addSpace len=len    />/* preconfiguration */
    ngx_http_${module.name}${filter}_init,<@addSpace len=len1+17/>/* postconfiguration */

    NULL,                                 <@addSpace len=len    />/* create main configuration */
    NULL,                                 <@addSpace len=len    />/* init main configuration */

    NULL,                                 <@addSpace len=len    />/* create server configuration */
    NULL,                                 <@addSpace len=len    />/* merge server configuration */

    ngx_http_${module.name}_create_loc_conf,<@addSpace len=len+6/>/* create location configuration */
    ngx_http_${module.name}_merge_loc_conf  <@addSpace len=len+6/>/* merge location configuration */
};


ngx_module_t  ngx_http_${module.name}${filter}_module = {
    NGX_MODULE_V1,
    &ngx_http_${module.name}${filter}_module_ctx, <@addSpace len=len1+3/>/* module context */
    ngx_http_${module.name}${filter}_commands,    <@addSpace len=len1+3/>/* module directives */
    NGX_HTTP_MODULE,                    /* module type */
    NULL,                               /* init master */
    NULL,                               /* init module */
    NULL,                               /* init process */
    NULL,                               /* init thread */
    NULL,                               /* exit thread */
    NULL,                               /* exit process */
    NULL,                               /* exit master */
    NGX_MODULE_V1_PADDING
};
</#macro>






<#macro conf module>
static void * ngx_http_${module.name}_create_loc_conf(ngx_conf_t *cf)
{
    ngx_http_${module.name}_loc_conf_t  *conf;

    conf = ngx_pcalloc(cf->pool, sizeof(ngx_http_${module.name}_loc_conf_t));
    if (conf == NULL) {
        return NULL;
    }
    <#if module.arrayParameters?size gt 0>
    /*
     * set by ngx_pcalloc():
     *<#list module.arrayParameters as one>
     *     conf->${one.name} = { NULL };
     *     conf->${one.name}_keys = NULL;</#list>
     */
    </#if>
    <#list module.parameters as one>
<#if     one.typeName = 'ngx_flag_t'>    conf-><@print str=one.name width=pLen /> = NGX_CONF_UNSET;
<#elseif one.typeName = 'ngx_uint_t'>    conf-><@print str=one.name width=pLen /> = NGX_CONF_UNSET_UINT;
</#if></#list>
    return conf;
}


static char * ngx_http_${module.name}_merge_loc_conf(ngx_conf_t *cf, void *parent, void *child)
{
    ngx_http_${module.name}_loc_conf_t  *prev = parent;
    ngx_http_${module.name}_loc_conf_t  *conf = child;
<#list module.parameters as one>
<#if     one.typeName = 'ngx_flag_t' >	ngx_conf_merge_value     (conf-><@print str=one.name width=pLen />, prev-><@print str=one.name width=pLen />, ${one.defaultValue});
<#elseif one.typeName = 'ngx_str_t'  >	ngx_conf_merge_str_value (conf-><@print str=one.name width=pLen />, prev-><@print str=one.name width=pLen />, "${one.defaultValue}");
<#elseif one.typeName = 'ngx_uint_t' >	ngx_conf_merge_uint_value(conf-><@print str=one.name width=pLen />, prev-><@print str=one.name width=pLen />, ${one.defaultValue});
<#elseif one.typeName = 'ngx_array_t'>
    if (ngx_http_merge_types(cf, &conf->${one.name}_keys, &conf->${one.name},&prev->${one.name}_keys, &prev->${one.name}, ngx_http_concat_default_types)
        != NGX_OK)
    {
        return NGX_CONF_ERROR;
    }</#if></#list>
    
    return NGX_CONF_OK;
}
</#macro>