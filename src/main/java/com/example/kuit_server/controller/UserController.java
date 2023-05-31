package com.example.kuit_server.controller;

import com.example.kuit_server.common.exception.UserException;
import com.example.kuit_server.common.response.BaseResponse;
import com.example.kuit_server.dto.PostLoginReq;
import com.example.kuit_server.dto.PostLoginRes;
import com.example.kuit_server.dto.PostUserReq;
import com.example.kuit_server.dto.PostUserRes;
import com.example.kuit_server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.kuit_server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static com.example.kuit_server.utils.BindingResultUtils.getErrorMessages;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public BaseResponse<PostUserRes> signUp(
            @Validated @RequestBody PostUserReq postUserReq, BindingResult bindingResult) throws UserException {
        if(bindingResult.hasErrors()){
            throw new UserException(INVALID_USER_VALUE,getErrorMessages(bindingResult));
        }
        PostUserRes postUserRes = userService.signUp(postUserReq);
        return new BaseResponse<>(postUserRes);
    }

    @PostMapping("/login/{id}")
    public BaseResponse<PostLoginRes> login(@Validated @RequestBody PostLoginReq postLoginRequest, BindingResult bindingResult,
                                            @PathVariable int id) {
        log.info("[UserController.login] userId={}", id);
        if (bindingResult.hasErrors()) {
            throw new UserException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        return new BaseResponse<>(userService.login(postLoginRequest, id));
    }


}
