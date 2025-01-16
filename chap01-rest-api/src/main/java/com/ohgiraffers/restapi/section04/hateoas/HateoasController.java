package com.ohgiraffers.restapi.section04.hateoas;

import com.ohgiraffers.restapi.section02.respsonsseentity.ResponseMessage;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/hateoas")
public class HateoasController {

    /* comment.
    *   Hateoas 란?
    *   하나의 API 를 동작 시켰을 때, 그전 상황이나 그후 상황에 대한
    *   충분한 정보(링크)를 제공함으로서
    *   클라이언트가 리소스(자원)에 대한 이해를 더욱 잘 할 수 있게
    *   링크를 제공하는 것.
    *   RestFul API 라고도 한다 -> 정보가 충분한*/
    // 상세조회할수있는 것도 제공해준다.

    private List<UserDTO> users;

    public HateoasController(){

        users = new ArrayList<>();
        users.add(new UserDTO(1,"user01","pass01","푸바오", LocalDate.now()));
        users.add(new UserDTO(2,"user02","pass02","후이바오", LocalDate.now()));
        users.add(new UserDTO(3,"user03","pass03","루이바오", LocalDate.now()));
    }

    @GetMapping("/users/{userNo}")
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        UserDTO foundUser =
                users.stream().filter(user -> user.getNo() == userNo)
                        .collect(Collectors.toList()).get(0);

        Map<String,Object> response = new HashMap<>();
        response.put("user",foundUser);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200,"조회성공",response));
    }

    /* Hateoas 적용 전체 조회 로직 API */
    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> findAllUsers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        /* EntityModel? Hateoas 에서 제공하는 클래스로 RestAPI 를 구성할 때
        *  해당 리소스와 연관이 있는 링크를 포함할 수 있도록 함.
        *  */
        List<EntityModel<UserDTO>> userLink = users.stream().map(
                user -> EntityModel.of(
                        user,
                        // withSelfRel() 특정 리소스를 조회하는 URL 링크를 제공하며
                        // 해당 링크를 제공함으로서 특정 리소스에 대한 정보를 알 수 있다.
                        linkTo(methodOn(HateoasController.class).findUserByNo(user.getNo())).withSelfRel(),
                        // withRel() : 현재 리소스와 관련 된 관계를 나타낸다.
                        linkTo(methodOn(HateoasController.class).findAllUsers()).withRel("users")
                )
        ).collect(Collectors.toList());

        // 위에 만든 데이터 응답 양식에 맞춰 넣기
        Map<String, Object> response = new HashMap<>();
        response.put("user",userLink);

        ResponseMessage message = new ResponseMessage(200,"조회성공!!",response);

        return new ResponseEntity<>(message,headers, HttpStatus.OK);
    }
}
