package com.example.api.RestApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class RestApi {

    @GetMapping(value="movie",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public String getAPI() {
       
      HashMap<String, Object> result = new HashMap<String, Object>();
      String jsonInString = "";

      try {
          RestTemplate restTemplate = new RestTemplate();

          HttpHeaders header = new HttpHeaders();
          HttpEntity<?> entity = new HttpEntity<>(header);
          String url = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";

          UriComponents uri = UriComponentsBuilder.fromHttpUrl(url+"?"+"key=1dc4addde2e1575912ba4adf02fb0278&targetDt=20220901").build();

          //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
          ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
          result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
          result.put("header", resultMap.getHeaders()); //헤더 정보 확인
          result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

          LinkedHashMap lm = (LinkedHashMap)resultMap.getBody().get("boxOfficeResult");
          ArrayList<Map> dboxoffList = (ArrayList<Map>)lm.get("dailyBoxOfficeList");
          LinkedHashMap mnList = new LinkedHashMap<>();
          for (Map obj : dboxoffList) {
              mnList.put(obj.get("rnum"),obj.get("movieNm"));
          }
          ObjectMapper mapper = new ObjectMapper();
          jsonInString = mapper.writeValueAsString(mnList);

      } catch (HttpClientErrorException | HttpServerErrorException e) {
          result.put("statusCode", e.getRawStatusCode());
          result.put("body"  , e.getStatusText());
          System.out.println(e.toString());

      } catch (Exception e) {
          result.put("statusCode", "999");
          result.put("body"  , "excpetion오류");
          System.out.println(e.toString());
      }
      return jsonInString;
  }
}