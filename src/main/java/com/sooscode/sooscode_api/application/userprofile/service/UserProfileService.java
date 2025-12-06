package com.sooscode.sooscode_api.application.userprofile.service;

import com.sooscode.sooscode_api.application.userprofile.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.userprofile.dto.UpdateProfileRequest;
import com.sooscode.sooscode_api.domain.user.entity.User;

public interface UserProfileService {
    void updatePassword(User user, UpdatePasswordRequest request);
    User updateProfile(User user, UpdateProfileRequest request);
    void deleteUser(User user);
}
