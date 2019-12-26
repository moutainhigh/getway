package com.ebay.getway.controller;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Primary
@EnableSwagger2
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {

    //RouteLocator可以根据zuul配置的路由列表获取服务
    private final RouteLocator routeLocator;

    public GatewaySwaggerResourcesProvider(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    //这个方法用来添加swagger的数据源
    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList();
        List<Route> routes = routeLocator.getRoutes();
        //通过RouteLocator获取路由配置，遍历获取所配置服务的接口文档，这样不需要手动添加，实现动态获取
        for (Route route: routes) {
            resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v2/api-docs"),"2.0"));
        }
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}