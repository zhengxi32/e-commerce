package com.xi.controller;

import com.alibaba.druid.filter.config.ConfigTools;
import com.xi.common.Response;
import com.xi.entity.dto.ProdDto;
import com.xi.mapper.ProdMapper;
import com.xi.service.ProdService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@RestController
@RequestMapping("/prod")
public class ProdController {

    @Resource
    private ProdService prodService;

    @GetMapping
    public Response<ProdDto> getProdDetail(@RequestParam String prodCode) {
        return Response.success(prodService.getProdDtoByProdCode(prodCode));
    }

}
