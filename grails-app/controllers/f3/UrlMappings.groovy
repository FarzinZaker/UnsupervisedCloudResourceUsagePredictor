package f3

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'monitoring', action: 'display')
        "/api/$type?"(controller: '  API', action: 'predict')
        "/api/resource/$resource/$type?"(controller: 'API', action: 'predict')
        "/api/resource/$resource/metric/$metric/$type?"(controller: 'API', action: 'predict')
        "/api/metric/$metric/$type?"(controller: 'API', action: 'predict')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
