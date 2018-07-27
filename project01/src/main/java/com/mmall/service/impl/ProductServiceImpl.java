package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.ipc.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse SaveOrUpdateProduct(Product product){
        if(product==null){
            return ServerResponse.createByErrorMessage("Product should not be empty");
        }

        if(StringUtils.isNotBlank(product.getSubImages())){
            String[] subImages= product.getSubImages().split(",");
            if(subImages.length>0){
                product.setMainImage(subImages[0]);
            }

        }
        int rowCount=0;
        if(product.getId()==null){
            rowCount=productMapper.insert(product);
        }else{
            rowCount=productMapper.updateByPrimaryKeySelective(product);
        }
        if(rowCount>0){
            return ServerResponse.createBySuccess(product);
        }
        return ServerResponse.createByErrorMessage("Failed");
    }


    public ServerResponse<String> Set_Sale_Status(Integer productId, Integer status){
        if(productId==null || status==null){
            return ServerResponse.createByErrorcodeMessage(ResponseCode.ILIGAL_ARGUMENT.getCode(),ResponseCode.ILIGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("SUCCESS");
        }
        return ServerResponse.createByErrorMessage("failed");
    }

    public ServerResponse<ProductDetailVo> manageProductDetails(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorcodeMessage(ResponseCode.ILIGAL_ARGUMENT.getCode(),ResponseCode.ILIGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("product withdrawed");
        }
        ProductDetailVo productDetailVo = assembleProductDetails(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ProductDetailVo assembleProductDetails(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());


        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse getProductLists(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.getList();
        List<ProductListVo> productListVos= Lists.newArrayList();
        for(Product product:products){
            ProductListVo productListVo=assmebleProductListVo(product);
            productListVos.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ProductListVo assmebleProductListVo(Product product){
        ProductListVo productListVo= new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());

        return productListVo;
    }


    public ServerResponse productSearch(String productName, Integer productId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.productSearch();
        List<ProductListVo> productListVos= Lists.newArrayList();
        for(Product product:products){
            ProductListVo productListVo=assmebleProductListVo(product);
            productListVos.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }



}
