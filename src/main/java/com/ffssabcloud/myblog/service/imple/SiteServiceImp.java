package com.ffssabcloud.myblog.service.imple;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ffssabcloud.myblog.domain.Archive;
import com.ffssabcloud.myblog.domain.Article;
import com.ffssabcloud.myblog.domain.ArticleExample;
import com.ffssabcloud.myblog.domain.ArticleExample.Criteria;
import com.ffssabcloud.myblog.domain.Meta;
import com.ffssabcloud.myblog.domain.MetaExample;
import com.ffssabcloud.myblog.domain.dao.ArticleMapper;
import com.ffssabcloud.myblog.domain.dao.MetaMapper;
import com.ffssabcloud.myblog.exception.PromptException;
import com.ffssabcloud.myblog.service.SiteService;
import com.ffssabcloud.myblog.utils.DateUtils;

@Service
public class SiteServiceImp implements SiteService{
    
    @Autowired
    ArticleMapper articleMapper;
    
    @Autowired
    MetaMapper metaMapper;
    
    @Override
    public List<Meta> getMetas(String type) {
        MetaExample example = new MetaExample();
        example.createCriteria().andTypeEqualTo(type);
        List<Meta> metas = metaMapper.selectByExample(example);
        
        return metas;
    }

    @Override
    public void setMeta(String type, String name) {
        if(StringUtils.isBlank(name)) {
            throw new PromptException("类别名不能为空!");
        }
        if(checkMetaExist(name)) {
            throw new PromptException("类别名已存在!");
        }
        
        Meta meta = new Meta();
        meta.setCount(0);
        meta.setName(name);
        meta.setType(type);
        
        metaMapper.insert(meta);
 
    }
    
    @Override
    public void setMetas(String type, String[] names) {
        for(String name : names) {
            setMeta(type, name);
        }
    }
    
    @Override
    public List<Archive> getArchives() {
        List<Archive> archives = getArchivesWithoutArticles();
        if(archives != null) {
            for(Archive archive : archives) {
                ArticleExample example = new ArticleExample();
                Criteria criteria = example.createCriteria();
                example.setOrderByClause("createAt desc");
                String strDate = archive.getDate();
                Date date = DateUtils.fmtStrDate(strDate, "yyyy-MM");
                int start = DateUtils.getUnixTimeByDate(date);
                int end = DateUtils.getUnixTimeByDate(
                        DateUtils.addDate(date, DateUtils.MONTH, 1));
                criteria.andCreateatGreaterThan(start);
                criteria.andCreateatLessThan(end);
                List<Article> articles = articleMapper.selectByExample(example);
                archive.setArticles(articles);
            }
        }
        return archives;
    }

    @Override
    public List<Archive> getArchivesWithoutArticles() {
        return articleMapper.selectArchives();
    }

    @Override
    public boolean checkMetaExist(String name) {
        MetaExample example = new MetaExample();
        example.createCriteria().andNameEqualTo(name);
        
        if(metaMapper.countByExample(example) == 0) {
            return false;
        }
        
        return true;
    }

    

}
