<#import "module_base.ftl" as base>
<#macro filter_module module>
/*
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 */

<@base.define module=module/>

static ngx_http_output_header_filter_pt _ngx_http_next_header_filter;
static ngx_http_output_body_filter_pt   _ngx_http_next_body_filter;


static ngx_int_t _ngx_http_${module.name}_header_filter(ngx_http_request_t *r)
{
    return _ngx_http_next_header_filter(r);
}


static ngx_int_t _ngx_http_${module.name}_body_filter(ngx_http_request_t *r, ngx_chain_t *in)
{
    return _ngx_http_next_body_filter(r, in);
}


<@base.conf module=module/>

static ngx_int_t ngx_http_${module.name}_filter_init(ngx_conf_t *cf)
{
    _ngx_http_next_body_filter = ngx_http_top_body_filter;
    ngx_http_top_body_filter = _ngx_http_${module.name}_body_filter;

    _ngx_http_next_header_filter = ngx_http_top_header_filter;
    ngx_http_top_header_filter = _ngx_http_${module.name}_header_filter;

    return NGX_OK;
}
</#macro>











<#macro handle_module module>
/*
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 */
<@base.define module=module filter=""/>


static ngx_int_t ngx_http_${module.name}_handler(ngx_http_request_t *req)
{
    return ngx_http_output_filter(req, &out);
}

<@base.conf module=module/>

static ngx_int_t ngx_http_${module.name}_init(ngx_conf_t *cf)
{
    ngx_http_handler_pt       *h;
    ngx_http_core_main_conf_t *cmcf;

    cmcf = ngx_http_conf_get_module_main_conf(cf, ngx_http_core_module);

    h = ngx_array_push(&cmcf->phases[NGX_HTTP_CONTENT_PHASE].handlers);
    if (h == NULL) {
        return NGX_ERROR;
    }

    *h = ngx_http_${module.name}_handler;
    return NGX_OK;
}
</#macro>

<#macro main module>
<#if module.filter>
	<@filter_module module=bean/>
<#else>
	<@handle_module module=bean/>
</#if>
</#macro>
<@main module=bean/>