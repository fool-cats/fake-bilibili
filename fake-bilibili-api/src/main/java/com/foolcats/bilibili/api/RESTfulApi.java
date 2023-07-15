package com.foolcats.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RESTfulApi {

//    Map is a good way to store information in memory
//    mock database
    private Map<Integer,Map<String,Object>> map;

    public RESTfulApi(){
        map=new HashMap<>();

        for(int i=0;i<3;i++){
            Map<String,Object> tempMap = new HashMap<>();
            tempMap.put("id",i);
            tempMap.put("name","name"+i);
            map.put(i,tempMap);
        }
    }

    @GetMapping("/mock/{id}")
    public Map<String,Object> getMock(@PathVariable Integer id){
        return map.get(id);
    }


    @DeleteMapping("/mock/{id}")
    public String deleteMock(@PathVariable Integer id){
        map.remove(id);
        return "delete successfully";
    }


    @PostMapping("/mock")
    public String postMock(@RequestBody Map<String,Object> postData){

        Integer[] idArray = map.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length - 1] + 1;
        map.put(nextId,postData);
        return "post successfully";
    }


    @PutMapping("/mock")
    public String putMock(@RequestBody Map<String,Object> putData){
//        Object --> String --> Integer
        Integer id = Integer.valueOf(String.valueOf(putData.get(("id"))));
        Map<String,Object> containsData = map.get(id);
        if(containsData == null){
            Integer[] idArray = map.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length - 1] + 1;
            map.put(nextId,putData);
        }else {
            map.put(id,putData);
        }
        return "put successfully";
    }




}
