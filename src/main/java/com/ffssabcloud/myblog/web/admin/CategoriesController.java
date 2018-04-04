package com.ffssabcloud.myblog.web.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ffssabcloud.myblog.constant.Constrants;
import com.ffssabcloud.myblog.domain.Meta;
import com.ffssabcloud.myblog.exception.PromptException;
import com.ffssabcloud.myblog.modal.bo.RestResponseBo;
import com.ffssabcloud.myblog.service.SiteService;

@Controller
@RequestMapping(value = "/admin/categories")
public class CategoriesController {
    
    @Autowired
    SiteService siteService;
    
    @GetMapping(value = "")
    public String categories(HttpServletRequest request) {
        List<Meta> metas = siteService.getMetas(Constrants.Types.CATEGORIES);
        request.setAttribute("categories", metas);
        
        return "admin/categories";
    }
    
    @PostMapping(value = "")
    @ResponseBody
    public RestResponseBo categories(HttpServletRequest request,
                                        @RequestParam String cg) {
        try {
            siteService.setMeta(Constrants.Types.CATEGORIES, cg);
        } catch(Exception e) {
            if(e instanceof PromptException) {
                return RestResponseBo.fail(e.getMessage());
            }
        }
        
        return RestResponseBo.ok("添加类别成功!");
    }
}
