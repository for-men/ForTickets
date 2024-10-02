package com.fortickets.userservice.application.mapper;

import com.fortickets.userservice.application.dto.requset.UpdateUserReq;
import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public GetUserRes userToGetUserRes(User user) {
        return new GetUserRes(user.getUserId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getProfileImage());
    }

    public void updateUserReqToUser(User user, UpdateUserReq req, String encodedPassword) {
        user.updateUserInfo(req.nickname(), encodedPassword, req.phone(), req.profileImage());
    }
}
