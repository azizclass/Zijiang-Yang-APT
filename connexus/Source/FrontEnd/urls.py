import os
import jinja2

# Definition of urls of different pages
URL_MANAGEMENT_PAGE = "/manage"
URL_LOGIN_PAGE = "/login"
URL_CREATE_STREAM_PAGE = "/create_stream"
URL_VIEW_STREAM_PAGE = "/view_stream"
URL_VIEW_ALL_STREAMS_PAGE = "/view_all_streams"
URL_SEARCH_STREAM_PAGE = "/search_stream"
URL_TRENDING_STREAMS_PAGE = "/trending_streams"
URL_ERROR_PAGE = "/error"
URL_UPLOAD_IMG = "/upload"


# Return an URL dictionary that can be used for jinja template rendering
def getUrlDir():
    dic = {
        'management_addr': URL_MANAGEMENT_PAGE,
        'login_addr': URL_LOGIN_PAGE,
        'create_stream_addr': URL_CREATE_STREAM_PAGE,
        'view_stream_addr': URL_VIEW_STREAM_PAGE,
        'view_all_streams_addr': URL_VIEW_ALL_STREAMS_PAGE,
        'search_stream_addr': URL_SEARCH_STREAM_PAGE,
        'trending_streams_addr': URL_TRENDING_STREAMS_PAGE,
        'error_addr': URL_ERROR_PAGE
    }
    return dic


# Get a template from a template name
def getTemplate(name):
    path = os.path.dirname(__file__)
    path = os.path.dirname(path)
    path = os.path.dirname(path)
    path = path + '\html_templates'
    JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader(path),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)
    return JINJA_ENVIRONMENT.get_template(name)