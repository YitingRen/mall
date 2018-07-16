package com.mmall.service.impl;


import com.github.pagehelper.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    private Logger logger = (Logger) LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse addCategory(String CategoryName, Integer parentId){
        if(StringUtils.isBlank(CategoryName) || parentId ==null){
            return ServerResponse.createByErrorMessage("Paremeter Error");
        }
        Category category = new Category();
        category.setName(CategoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("Success");
        }
        return ServerResponse.createByErrorMessage("Error");

    }

    public ServerResponse setCategoryName(Integer CategoryId, String CategoryName){
        if(StringUtils.isBlank(CategoryName) || CategoryId==null){
            return ServerResponse.createByErrorMessage("parameter Error");
        }
        Category category = new Category();
        category.setId(CategoryId);
        category.setName(CategoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("SUCCESS");
        }
        return ServerResponse.createByErrorMessage("ERROR");

    }


    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.getChildrenByparentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("not found");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        selectChildrenCategoryById(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryIdList!=null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> selectChildrenCategoryById(Set<Category> categoryIdList, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        categoryIdList.add(category);
        List<Category> categoryList=categoryMapper.getChildrenByparentId(categoryId);
        for(Category categoryItem:categoryList){
            selectChildrenCategoryById(categoryIdList,categoryItem.getId());
        }
        return categoryIdList;
    }





}
