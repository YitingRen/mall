package com.mmall.controller.backend;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String CategoryName, @RequestParam(value = "parantId",defaultValue = "0")int parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user ==null){
            return ServerResponse.createByErrorMessage("log in please");
        }
        //check is the user is admin
        if(iUserService.checkAdmin(user).isSuccess()){
            if(iCategoryService.addCategory(CategoryName,parentId).isSuccess()){
                return ServerResponse.createBySuccessMessage("SUCCESS");
            }
            return ServerResponse.createByErrorMessage("Error");
        }else{
            return ServerResponse.createByErrorMessage("Error, you are not the admin");
        }
    }


    @RequestMapping("set_category.do")
    @ResponseBody
    public ServerResponse SetCategoryName(HttpSession session, int categoryId, String CategoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("log in please");
        }
        //check if user is admin
        if(iUserService.checkAdmin(user).isSuccess()){
            return iCategoryService.setCategoryName(categoryId,CategoryName);
        }
        return ServerResponse.createByErrorMessage("Not admin");
    }


    @RequestMapping("get_ChildrenParallel_Category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "parentid",defaultValue = "0") int parent_id){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorMessage("log in please");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(parent_id);
        }
        return ServerResponse.createByErrorMessage("You are not admin");
    }

    @RequestMapping("get_CategoryAndDeepChildren_Category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0") int categoryId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorMessage("log in please");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMessage("You are not admin");
    }

}
