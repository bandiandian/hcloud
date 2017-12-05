package com.interconn.haier.provider;

import com.interconn.haier.provider.domain.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  11:44
 */
public interface DemoService {

    @RequestMapping(value = "/sayHello", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String  sayHello( @RequestParam("name")  String name);

    @RequestMapping(value = "/createUser", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String  createUser(@RequestBody User user);

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    User  updateUser(@RequestBody User user);


    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    User  findByName(@RequestParam("name") String name);


    @RequestMapping(value = "/deleteByName", method = RequestMethod.DELETE)
    User  deleteByName(@RequestParam("id") String id,@RequestBody User user);


    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    User  findById(@PathVariable("id") Long id);


    @RequestMapping(value = "upload/test",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadMultiFile(@RequestParam(value = "file", required = true)MultipartFile file);


}
