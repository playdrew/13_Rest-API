package com.ohgiraffers.restapi.section02.respsonsseentity;

import com.ohgiraffers.restapi.section01.response.ResponseController;
import org.apache.catalina.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/entity")
public class ResponseEntityController {

    /* comment.
    *   ResponseEntity 란?
    *   결과 데이터 & HTTP 상태 코드를 직접 제어할 수 있는
    *   클래스 이다.
    *   내부에 HttpStatus , HttpHeaders, HttpBody 를 포함한다.
    * */

    private List<UserDTO> users; // 임시 db

    // 생성자
    /* 임시 DB 에서 조회한 값 설정 */
    public ResponseEntityController(){
        users = new ArrayList<>();

        users.add(new UserDTO(1,"user01","pass01","너구리", LocalDate.now()));
        users.add(new UserDTO(2,"user02","pass02","푸바오", LocalDate.now()));
        users.add(new UserDTO(3,"user03","pass03","러바오", LocalDate.now()));
    }

    // 리스폰스 엔티티는 제네릭타입을 설정하는데 클래스 생성
    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> findAllUsers(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users",users);

        ResponseMessage responseMessage =
                new ResponseMessage(200,"조회성공",responseMap);
        // responseMessage 를 구지 추가하는 이유는 상태코드와 메시지를 이용해서 프론트딴에서 상태에 따라 보여주기 위해서다.
        // 조회가 잘 되었다 OK NOTFOUND(404)
        return new ResponseEntity<>(responseMessage,headers, HttpStatus.OK);
    }

    // pk 로 유저 조회하기 상세 조회
    @GetMapping("/users/{userNo}")
    // List<UserDTO> 해도 됨 추가적 return users 단 추가적 커스터마이징이 불가
    // ResponseMessage ResponseEntity 를 쓴 이유는 정형화를 위해 추가적으로 쓸 데이터 구성하기위해서다.
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        UserDTO foundUser = users.stream().filter(user -> user.getNo() == userNo).collect(Collectors.toList()).get(0);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user",foundUser);

        // HttpStatus.ok 같은것 ResponseEntity.ok()
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200,"조회성공",responseMap));
    }

    /* comment.
    *   form 태그로 데이터 전달 받는 것과
    *   javaScript 로 데이터 전달 받는 것이 다르다.
    * */
    @PostMapping("/user/regist")
    // ? 와일드카드로 아무값이나 들어올 수 있게 처리
    // 패치로 포스트는 리퀘스트바디로 받았을껄
    public ResponseEntity<?> regist(@RequestBody UserDTO newUser){

        System.out.println("Json 데이터 @RequestBody 로 들어오니 : " + newUser);

        // List 에 들어있는 마지막 no 가져오기
        int lastNo = users.get(users.size()-1).getNo();
        newUser.setNo(lastNo + 1);

        // 등록할때는 ok 가 아닌 200이 아닌 201 created
        // 201 상태코드(자원 생성 관련) 상태 코드
        return ResponseEntity.created(URI.create("/entity/user/" + users.get(users.size()-1).getNo())).build();
    }

    /* 수정 */
    // user 1 번을 수정하겠다.
    @PutMapping("/users/{userNo}")
    public ResponseEntity<?> modifyUser(@PathVariable int userNo , @RequestBody UserDTO modifyInfo){
        // 회원 정보 수정을 위한 유저 특정하기
        UserDTO foundUser = users.stream().filter(user -> user.getNo()==userNo).collect(Collectors.toList()).get(0);

        // id , pwd , name 수정하기
        foundUser.setId(modifyInfo.getId());
        foundUser.setPwd(modifyInfo.getPwd());
        foundUser.setName(modifyInfo.getName());

        return ResponseEntity.created(URI.create("/entity/users/" + userNo)).build();
    }

    @DeleteMapping("/users/{userNo}")
    public ResponseEntity<?> removeUser(@PathVariable int userNo){

        // userNo 1명 특정
        UserDTO foundUser = users.stream().filter(user -> user.getNo()==userNo).collect(Collectors.toList()).get(0);

        // 특정한 유저 객체 배열에서 삭제
        users.remove(foundUser);

        // 자원 삭제 관련 noContent
        return ResponseEntity.noContent().build();

    }

}
