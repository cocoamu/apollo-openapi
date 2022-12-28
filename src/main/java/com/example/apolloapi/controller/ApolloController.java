package com.example.apolloapi.controller;

import com.alibaba.fastjson2.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceGrayDelReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenReleaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 操作apollo配置
 * @author: lizz
 * @date: 2020/7/15 11:30
 */
@RestController
@RequestMapping(value = "/apollo")
//@Profile({"dev","test"}) //内部环境使用
public class ApolloController {
    private static final Logger logger = LoggerFactory.getLogger(ApolloController.class);



    //apollo中项目id
    private final static String appId = "SampleApp";
    //apollo操作用户
    private final static String opUser = "apollo";
    //apollo中集群名称，apollo默认集群为default
    private final static String cluster = "default";
    //apollo中集群内namespace名称
    private final static String namespace = "mytest";

    //apollo操作客户端
    private ApolloOpenApiClient apolloClient;
    public ApolloController(ApolloOpenApiClient client) {
        this.apolloClient = client;
    }


    /**
     * 获取环境列表，如
     * [{"clusters":["huawei","default"],"env":"PRO"},{"clusters":["default"],"env":"DEV"}]
     * @param server apollo中服务id
     * @return
     */
    @GetMapping("/envclusters/{appid}")
    public Object getEnvclusters(@PathVariable String appid) {
        return JSON.toJSONString(apolloClient.getEnvClusterInfo(appid));
    }

    /**
     * 向apollo中新增配置项，为未发布状态。
     * post uri:apollo/dev/add
     * @param env 指定apollo的数据环境
     * @return
     */
    @PostMapping("/add/{env}")
    public Object addParam(@PathVariable String env) {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("hehehe");
        openItemDTO.setValue("100");
        openItemDTO.setComment("超时时间");
        openItemDTO.setDataChangeCreatedBy(opUser);
        OpenItemDTO item = apolloClient.createItem(appId, env, cluster, namespace, openItemDTO);
        return JSON.toJSONString(item);
    }

    /**
     * 修改apollo中配置项，为未发布状态。
     * post uri:apollo/dev/update
     * @param env 指定apollo的数据环境
     * @return
     */
    @PostMapping("/{env}/update")
    public Object updateParam(@PathVariable String env) {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("hehehe");
        openItemDTO.setValue("500");
        openItemDTO.setComment("超时时间");
        openItemDTO.setDataChangeCreatedBy(opUser);
        apolloClient.createOrUpdateItem(appId, env, cluster, namespace, openItemDTO);
        return JSON.toJSONString(openItemDTO);
    }

    /**
     * 获取apollo中namespace的所有配置项
     * @param env 指定apollo的数据环境
     * @return
     */
    @GetMapping("/{env}/namespace")
    public Object getAllNameSpace(@PathVariable String env) {
        return JSON.toJSONString(apolloClient.getNamespace(appId, env, cluster, "application"));
    }


    /**
     * 获取某一项配置
     * @param env 指定apollo的数据环境
     * @param key 配置项key
     * @return
     */
    @GetMapping("/{env}/getParam")
    public Object getParam(@PathVariable String env,String key) {
        OpenItemDTO getItem = apolloClient.getItem(appId, env, cluster, namespace, key);
        return JSON.toJSONString(getItem);
    }

    /**
     * 刷新发布配置
     *
     * @param env 指定apollo的数据环境
     * @return
     */
    @PostMapping("/{env}/releaseParam")
    public Object releaseParam(@PathVariable String env) {
        NamespaceGrayDelReleaseDTO namespaceGrayDelReleaseDTO = new NamespaceGrayDelReleaseDTO();
        //配置版本名称
        namespaceGrayDelReleaseDTO.setReleaseTitle(System.currentTimeMillis() + "-release");
        //刷新说明
        namespaceGrayDelReleaseDTO.setReleaseComment("auto release");
        namespaceGrayDelReleaseDTO.setReleasedBy(opUser);
        OpenReleaseDTO openReleaseDTO = apolloClient.publishNamespace(appId, env, cluster, namespace, namespaceGrayDelReleaseDTO);
        return JSON.toJSONString(openReleaseDTO);
    }
}