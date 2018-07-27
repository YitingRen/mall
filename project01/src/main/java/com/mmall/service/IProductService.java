package com.mmall.service;


import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

public interface IProductService {
    ServerResponse SaveOrUpdateProduct(Product product);
    ServerResponse<String> Set_Sale_Status(Integer productId, Integer status);
    ServerResponse<ProductDetailVo> manageProductDetails(Integer productId);
    ServerResponse getProductLists(Integer pageNum, Integer pageSize);
}
