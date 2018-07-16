package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")//we want to inject this interface to controller
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //数据段的相应对象放到common
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("username not exist");
        }


        //todo password login MD5
        String MD5Ppassword = MD5Util.MD5EncodeUtf8(password);

        User user =userMapper.selectLogin(username,password);

        if (user==null){
            return ServerResponse.createByErrorMessage("password error");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("SUCCESS",user);

    }


    public ServerResponse<String> register(User user){

        ServerResponse validResponse =this.checkValid(user.getUsername(),Const.username);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse =this.checkValid(user.getEmail(),Const.Email);
        if(!validResponse.isSuccess()){
            return validResponse;
        }


        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("Error");
        }
        return ServerResponse.createBySuccessMessage("SUCCESS");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNoneBlank(type)){
            if(Const.username.equals(type)){
                int resultCount= userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("user exist");
                }
            }
            if (Const.Email.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("email used");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("Parameter Error");
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validRespose =this.checkValid(username,Const.username);
        if (validRespose.isSuccess()){
            //user not exist
            return ServerResponse.createByErrorMessage("User not exist");
        }
        String question =userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("Question empty");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("answer error");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isNotBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("Parameter Error");
        }
        //if user not exists, return!
        ServerResponse validResponse = this.checkValid(username,Const.username);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("user not exists");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token expired");
        }

        if(StringUtils.equals(forgetToken,token)){
            String md5Password =MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount =userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("SUCCESS");
            }
        }else{
            return ServerResponse.createByErrorMessage("token wrong");
        }
        return ServerResponse.createByErrorMessage("failed");


    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount>0){
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            int updateCount=userMapper.updateByPrimaryKeySelective(user);
            if(updateCount>0){
                return ServerResponse.createBySuccessMessage("SUCCESS");
            }
            return ServerResponse.createByErrorMessage("password update error");
        }else{
            return ServerResponse.createByErrorMessage("password wrong");
        }
    }

    public ServerResponse<User> updateInformation(User user){
        //check email exists
        int resultCount = userMapper.checkEmailByuserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email exists");
        }
        User updateUser= new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccess("SUCCESS",updateUser);
        }

        return ServerResponse.createByErrorMessage("update failed");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user==null){
            ServerResponse.createByErrorMessage("cannot find current user");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse<String> checkAdmin(User user){
        if (user ==null){
            return ServerResponse.createByErrorMessage("no user login");
        }
        if (user.getRole()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("you cannot access");
        }else{
            return ServerResponse.createBySuccess();
        }

    }
}
