package com.pms.setting.projects.info.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "G_PROJECT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GProject {

    @EmbeddedId
    private GProjectId id; // 복합키 적용

    @MapsId("projectNo") // GProjectId의 projectNo와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_NO")
    private Project project;

    @MapsId("groupNo") // GProjectId의 groupNo와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_NO")
    private Group group;

    @Column(name = "PM")
    private Integer pm;

    // 생성자 수정
    public GProject(Project project, Group group, Integer pm) {
        this.project = project;
        this.group = group;
        this.id = new GProjectId(project.getProjectNo(), group.getGroupNo());
        this.pm = pm;
    }
}