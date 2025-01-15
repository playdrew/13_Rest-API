package com.ohgiraffers.restapi.section03.vaild;

import com.ohgiraffers.restapi.section02.respsonsseentity.ResponseMessage;
import com.ohgiraffers.restapi.section02.respsonsseentity.UserDTO;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/valid")
public class ValidTestController {

    private List<UserDTO> users; // 임시 db

    // 생성자
    /* 임시 DB 에서 조회한 값 설정 */
    public ValidTestController(){
        users = new ArrayList<>();

        users.add(new UserDTO(1,"user01","pass01","너구리", LocalDate.now()));
        users.add(new UserDTO(2,"user02","pass02","푸바오", LocalDate.now()));
        users.add(new UserDTO(3,"user03","pass03","러바오", LocalDate.now()));
    }

    // userNo 10이라는 값을 넣으면 어떻게 될까요. null 값
    @GetMapping("/user/{userNo}")
    public ResponseEntity<ResponseMessage> findByNo(@PathVariable int userNo) throws UserNotFoundException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        List<UserDTO> foundUserList =
                users.stream().filter(user -> user.getNo() == userNo).collect(Collectors.toList());

        UserDTO foundUser = null;

        // userNo 조회시 조회됨
        if(foundUserList.size()>0){
            foundUser = foundUserList.get(0);
        } else{
            throw new UserNotFoundException("회원 정보를 찾을 수 없습니다.");
        }
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user",foundUser);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseMessage(200,"조회성공!!",responseMap));
    }

    @PostMapping("/user/regist")
    public ResponseEntity<?> registUser(@Valid @RequestBody com.ohgiraffers.restapi.section03.vaild.UserDTO newUser){

        System.out.println("newUser = " + newUser);

        return ResponseEntity.created(URI.create("/valid/user/"+ newUser.getNo())).build();
    }

}
