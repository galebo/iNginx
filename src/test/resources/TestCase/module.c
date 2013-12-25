/*
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 */

#include <ngx_config.h>
#include <ngx_core.h>
#include <ngx_http.h>


typedef struct {
    ngx_flag_t   enable;
    ngx_uint_t   max_files;
    ngx_flag_t   unique;
    ngx_str_t    delimiter;
    ngx_flag_t   ignore_file_error;
	
    ngx_hash_t   types;
    ngx_array_t *types_keys;
} ngx_http_concat_loc_conf_t;


static void *ngx_http_concat_create_loc_conf(ngx_conf_t *cf);
static char *ngx_http_concat_merge_loc_conf(ngx_conf_t *cf,void *parent, void *child);
static ngx_int_t ngx_http_concat_init(ngx_conf_t *cf);

static ngx_str_t  ngx_http_concat_default_types[] = {
    ngx_string("application/x-javascript"),
    ngx_string("text/css"),
    ngx_null_string
};

static ngx_command_t  ngx_http_concat_commands[] = {

    { ngx_string("concat"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_FLAG,
      ngx_conf_set_flag_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, enable),
      NULL },
      
    { ngx_string("concat_max_files"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_num_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, max_files),
      NULL },
      
    { ngx_string("concat_unique"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_FLAG,
      ngx_conf_set_flag_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, unique),
      NULL },
      
    { ngx_string("concat_delimiter"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_TAKE1,
      ngx_conf_set_str_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, delimiter),
      NULL },
      
    { ngx_string("concat_ignore_file_error"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_FLAG,
      ngx_conf_set_flag_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, ignore_file_error),
      NULL },
      
    { ngx_string("concat_types"),
      NGX_HTTP_MAIN_CONF|NGX_HTTP_SRV_CONF|NGX_HTTP_LOC_CONF|NGX_CONF_1MORE,
      ngx_http_types_slot,
      NGX_HTTP_LOC_CONF_OFFSET,
      offsetof(ngx_http_concat_loc_conf_t, types_keys),
      &ngx_http_concat_default_types[0] },
      
      ngx_null_command
};


static ngx_http_module_t  ngx_http_concat_module_ctx = {
    NULL,                                   /* preconfiguration */
    ngx_http_concat_init,                   /* postconfiguration */

    NULL,                                   /* create main configuration */
    NULL,                                   /* init main configuration */

    NULL,                                   /* create server configuration */
    NULL,                                   /* merge server configuration */

    ngx_http_concat_create_loc_conf,        /* create location configuration */
    ngx_http_concat_merge_loc_conf          /* merge location configuration */
};


ngx_module_t  ngx_http_concat_module = {
    NGX_MODULE_V1,
    &ngx_http_concat_module_ctx,      /* module context */
    ngx_http_concat_commands,         /* module directives */
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


static ngx_int_t ngx_http_concat_handler(ngx_http_request_t *req)
{
    return ngx_http_output_filter(req, &out);
}

static void * ngx_http_concat_create_loc_conf(ngx_conf_t *cf)
{
    ngx_http_concat_loc_conf_t  *conf;

    conf = ngx_pcalloc(cf->pool, sizeof(ngx_http_concat_loc_conf_t));
    if (conf == NULL) {
        return NULL;
    }
    /*
     * set by ngx_pcalloc():
     *
     *     conf->types = { NULL };
     *     conf->types_keys = NULL;
     */
    conf->enable             = NGX_CONF_UNSET;
    conf->max_files          = NGX_CONF_UNSET_UINT;
    conf->unique             = NGX_CONF_UNSET;
    conf->ignore_file_error  = NGX_CONF_UNSET;
    return conf;
}


static char * ngx_http_concat_merge_loc_conf(ngx_conf_t *cf, void *parent, void *child)
{
    ngx_http_concat_loc_conf_t  *prev = parent;
    ngx_http_concat_loc_conf_t  *conf = child;
	ngx_conf_merge_value     (conf->enable            , prev->enable            , 0);
	ngx_conf_merge_uint_value(conf->max_files         , prev->max_files         , 10);
	ngx_conf_merge_value     (conf->unique            , prev->unique            , 1);
	ngx_conf_merge_str_value (conf->delimiter         , prev->delimiter         , "");
	ngx_conf_merge_value     (conf->ignore_file_error , prev->ignore_file_error , 0);
    if (ngx_http_merge_types(cf, &conf->types_keys, &conf->types,&prev->types_keys, &prev->types, ngx_http_concat_default_types)
        != NGX_OK)
    {
        return NGX_CONF_ERROR;
    }
    
    return NGX_CONF_OK;
}

static ngx_int_t ngx_http_concat_init(ngx_conf_t *cf)
{
    ngx_http_handler_pt       *h;
    ngx_http_core_main_conf_t *cmcf;

    cmcf = ngx_http_conf_get_module_main_conf(cf, ngx_http_core_module);

    h = ngx_array_push(&cmcf->phases[NGX_HTTP_CONTENT_PHASE].handlers);
    if (h == NULL) {
        return NGX_ERROR;
    }

    *h = ngx_http_concat_handler;
    return NGX_OK;
}
