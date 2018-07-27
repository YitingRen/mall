package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse SaveOrUpdateProduct(HttpSession session,Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("you are not login");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.SaveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("You are not admin");
        }

    }

    @RequestMapping("Set_Sale_Status.do")
    @ResponseBody
    public ServerResponse Set_Sale_Status(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("you are not login");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.Set_Sale_Status(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("You are not admin");
        }
    }


    @RequestMapping("getDetail.do")
    @ResponseBody
    public ServerResponse getDetails(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("you are not login");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.manageProductDetails(productId);
        }else{
            return ServerResponse.createByErrorMessage("You are not admin");
        }
    }


    @RequestMapping("getList.do")
    @ResponseBody

    public ServerResponse getLists(HttpSession session, @RequestParam(value="pageNum", defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("you are not login");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.getProductLists(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("You are not admin");
        }
    }


    @RequestMapping("productSearch.do")
    @ResponseBody

    public ServerResponse productSearch(HttpSession session,@RequestParam(value = "productName")String productName,@RequestParam(value="productId")Integer productId, @RequestParam(value="pageNum", defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("you are not login");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.getProductLists(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("You are not admin");
        }
    }


    public ServerResponse upload(HttpSession session, MultipartFile file, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        iFileService.upload(file,path);
        return ServerResponse.createBySuccess();
    }
}
