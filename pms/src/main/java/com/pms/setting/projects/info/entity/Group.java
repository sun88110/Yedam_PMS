package com.pms.setting.projects.info.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "GROUPS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {

    @Id
    @Column(name = "GROUP_NO")
    private Long groupNo;

    @Column(name = "GROUPNAME") // 📍 아까 찾은 그 이름! 언더바 없음!
    private String groupName;

    @Column(name = "STATUS")
    private Integer status;

    
}