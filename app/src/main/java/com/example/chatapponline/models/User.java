package com.example.chatapponline.models;

import java.io.Serializable;

//khai báo serializable cho phép đối tượng của 1 lớp thành luồng byte để lưu trữ
public class User implements Serializable {
    public String name, image, email, token, id;
}
