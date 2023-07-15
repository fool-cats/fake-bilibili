package com.foolcats.bilibili.service;

import com.foolcats.bilibili.dao.UserDao;
import com.foolcats.bilibili.domain.User;
import com.foolcats.bilibili.domain.UserInfo;
import com.foolcats.bilibili.domain.constant.UserConstant;
import com.foolcats.bilibili.domain.exception.ConditionException;
import com.foolcats.bilibili.service.utils.MD5Util;
import com.foolcats.bilibili.service.utils.RSAUtil;
import com.foolcats.bilibili.service.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void addUser(User user) {

        String phone = user.getPhone();
//        Invalid input
        if(StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("Phone Number Invalid: Null or Empty");
        }

        User dbUser = this.getUserByPhone(phone);

        if(dbUser != null) {
            throw new ConditionException("User already");
        }

//        Use current time to MD5 hash, It is a salt
        Date now = new Date();
//        date time ----> String
        String salt = String.valueOf(now.getTime());
//        Get the password from frontend
        String password = user.getPassword();

        String rawPassword;  // raw password
//        there is an exception, so ewe need to use try catch to handle this exception
        try {
//        decrypt password,because password is encrypted by frontend
            rawPassword =  RSAUtil.decrypt(password);
        }catch (Exception e) {
            throw new ConditionException("fail to decrypt password");
        }

//        after we get the raw password, we can use MD5 to encrypt it
        String md5Password = MD5Util.sign(rawPassword,salt,"UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);

//        finally, we need to add the user into database
        userDao.addUser(user);


//        after we created the user, we need to add user information

        UserInfo userInfo = new UserInfo();

        userInfo.setId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);    // default nickname is
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH); // default birth is 1999
        userInfo.setGender(UserConstant.GENDER_MALE);  // default gender is male
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
    }


//    @Transactional
//    public void addUser(User user) {
//        String phone = user.getPhone();
//        if(StringUtils.isNullOrEmpty(phone)){
//            throw new ConditionException("手机号不能为空！");
//        }
//        User dbUser = this.getUserByPhone(phone);
//        if(dbUser != null){
//            throw new ConditionException("该手机号已经注册！");
//        }
//        Date now = new Date();
//        String salt = String.valueOf(now.getTime());
//        String password = user.getPassword();
//        String rawPassword;
//        try{
//            rawPassword = RSAUtil.decrypt(password);
//        }catch (Exception e){
//            throw new ConditionException("密码解密失败！");
//        }
//        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
//        user.setSalt(salt);
//        user.setPassword(md5Password);
//        user.setCreateTime(now);
//        userDao.addUser(user);
//        //添加用户信息
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUserId(user.getId());
//        userInfo.setNick(UserConstant.DEFAULT_NICK);
//        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
//        userInfo.setGender(UserConstant.GENDER_MALE);
//        userInfo.setCreateTime(now);
//        userDao.addUserInfo(userInfo);
//        //添加用户默认权限角色
//        userAuthService.addUserDefaultRole(user.getId());
//        //同步用户信息数据到es
//        elasticSearchService.addUserInfo(userInfo);
//    }
//
    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) {
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();
        if(StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)){
            throw new ConditionException("Invalid phone number！ : null or empty");
        }
        User dbUser = userDao.getUserByPhone(phone);
        if(dbUser == null){
            throw new ConditionException("User doesn't exit！");
        }
        String password = user.getPassword();
        String rawPassword;
        try{
            rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new ConditionException("fail to decrypt password");
        }
//      Login is get salt, sign up is set salt
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("Wrong password！");
        }

        TokenUtil tokenUtil = new TokenUtil();
//        a valid password: generate token
        return tokenUtil.generateToken(dbUser.getId());
    }
//
//    public String login(User user) throws Exception{
//        String phone = user.getPhone() == null ? "" : user.getPhone();
//        String email = user.getEmail() == null ? "" : user.getEmail();
//        if(StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)){
//            throw new ConditionException("参数异常！");
//        }
//        User dbUser = userDao.getUserByPhoneOrEmail(phone, email);
//        if(dbUser == null){
//            throw new ConditionException("当前用户不存在！");
//        }
//        String password = user.getPassword();
//        String rawPassword;
//        try{
//            rawPassword = RSAUtil.decrypt(password);
//        }catch (Exception e){
//            throw new ConditionException("密码解密失败！");
//        }
//        String salt = dbUser.getSalt();
//        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
//        if(!md5Password.equals(dbUser.getPassword())){
//            throw new ConditionException("密码错误！");
//        }
//        return TokenUtil.generateToken(dbUser.getId());
//    }
//
//    public User getUserInfo(Long userId) {
//        User user = userDao.getUserById(userId);
//        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
//        user.setUserInfo(userInfo);
//        return user;
//    }
//
//    public void updateUsers(User user) throws Exception{
//        Long id = user.getId();
//        User dbUser = userDao.getUserById(id);
//        if(dbUser == null){
//            throw new ConditionException("用户不存在！");
//        }
//        if(!StringUtils.isNullOrEmpty(user.getPassword())){
//            String rawPassword = RSAUtil.decrypt(user.getPassword());
//            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
//            user.setPassword(md5Password);
//        }
//        user.setUpdateTime(new Date());
//        userDao.updateUsers(user);
//    }
//
//    public void updateUserInfos(UserInfo userInfo) {
//        userInfo.setUpdateTime(new Date());
//        userDao.updateUserInfos(userInfo);
//    }
//
//    public User getUserById(Long followingId) {
//        return userDao.getUserById(followingId);
//    }
//
//    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
//        return userDao.getUserInfoByUserIds(userIdList);
//    }
//
//    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
//        Integer no = params.getInteger("no");
//        Integer size = params.getInteger("size");
//        params.put("start", (no-1)*size);
//        params.put("limit", size);
//        Integer total = userDao.pageCountUserInfos(params);
//        List<UserInfo> list = new ArrayList<>();
//        if(total > 0){
//            list = userDao.pageListUserInfos(params);
//        }
//        return new PageResult<>(total, list);
//    }
//
//    public Map<String, Object> loginForDts(User user) throws Exception{
//        String phone = user.getPhone() == null ? "" : user.getPhone();
//        String email = user.getEmail() == null ? "" : user.getEmail();
//        if(StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)){
//            throw new ConditionException("参数异常！");
//        }
//        User dbUser = userDao.getUserByPhoneOrEmail(phone, email);
//        if(dbUser == null){
//            throw new ConditionException("当前用户不存在！");
//        }
//        String password = user.getPassword();
//        String rawPassword;
//        try{
//            rawPassword = RSAUtil.decrypt(password);
//        }catch (Exception e){
//            throw new ConditionException("密码解密失败！");
//        }
//        String salt = dbUser.getSalt();
//        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
//        if(!md5Password.equals(dbUser.getPassword())){
//            throw new ConditionException("密码错误！");
//        }
//        Long userId = dbUser.getId();
//        String accessToken = TokenUtil.generateToken(userId);
//        String refreshToken = TokenUtil.generateRefreshToken(userId);
//        //保存refresh token到数据库
//        userDao.deleteRefreshTokenByUserId(userId);
//        userDao.addRefreshToken(refreshToken, userId, new Date());
//        Map<String, Object> result = new HashMap<>();
//        result.put("accessToken", accessToken);
//        result.put("refreshToken", refreshToken);
//        return result;
//    }
//
//    public void logout(String refreshToken, Long userId) {
//        userDao.deleteRefreshToken(refreshToken, userId);
//    }
//
//    public String refreshAccessToken(String refreshToken) throws Exception {
//        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
//        if(refreshTokenDetail == null){
//            throw new ConditionException("555","token过期！");
//        }
//        Long userId = refreshTokenDetail.getUserId();
//        return TokenUtil.generateToken(userId);
//    }
//
//    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
//        return userDao.batchGetUserInfoByUserIds(userIdList);
//    }
//
//    public String getRefreshTokenByUserId(Long userId) {
//        return userDao.getRefreshTokenByUserId(userId);
//    }
}

