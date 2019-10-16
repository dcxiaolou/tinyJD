package com.dcxiaolou.tinyJD.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class UmsMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long member_level_id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String status;
    private Date create_time;
    private String icon;
    private String gender;
    private Date birthday;
    private String city;
    private String job;
    private String personalized_signature;
    private int source_type;
    private int integration;
    private int growth;
    private int lucky_count;
    private int history_integration;
    private String access_code;
    private String source_uid;
    private String access_token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMember_level_id() {
        return member_level_id;
    }

    public void setMember_level_id(Long member_level_id) {
        this.member_level_id = member_level_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPersonalized_signature() {
        return personalized_signature;
    }

    public void setPersonalized_signature(String personalized_signature) {
        this.personalized_signature = personalized_signature;
    }

    public int getSource_type() {
        return source_type;
    }

    public void setSource_type(int source_type) {
        this.source_type = source_type;
    }

    public int getIntegration() {
        return integration;
    }

    public void setIntegration(int integration) {
        this.integration = integration;
    }

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public int getLucky_count() {
        return lucky_count;
    }

    public void setLucky_count(int lucky_count) {
        this.lucky_count = lucky_count;
    }

    public int getHistory_integration() {
        return history_integration;
    }

    public void setHistory_integration(int history_integration) {
        this.history_integration = history_integration;
    }

    public String getAccess_code() {
        return access_code;
    }

    public void setAccess_code(String access_code) {
        this.access_code = access_code;
    }

    public String getSource_uid() {
        return source_uid;
    }

    public void setSource_uid(String source_uid) {
        this.source_uid = source_uid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
