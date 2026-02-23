package com.pms.setting.projects.info.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROJECT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROJECT_NO")
	private Long projectNo;

	@Column(name = "PROJECT_NAME")
	private String projectName;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "PROJECT_DESC")
	private String projectDesc;

	@Column(name = "PROJECT_HOME")
	private String projectHome;

	@Column(name = "PROJECT_CODE")
	private String projectCode;

	@Column(name = "CREATE_AT")
	private LocalDateTime createAt;

	@Column(name = "UPDATE_AT")
	private LocalDateTime updateAt;

	@Column(name = "PUBLIC_YN")
	private Integer publicYn;

	@Column(name = "STATUS")
	private Integer status;

	@Column(name = "PARENT_PROJECT_NO")
	private Long parentProjectNo;

	@Column(name = "START_DATE")
	private LocalDateTime startDate;

	@Column(name = "END_DATE")
	private LocalDateTime endDate;

	// 📍 그룹 매핑과의 관계 설정
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GProject> projectGroups = new ArrayList<>();

	public void updateBasicInfo(String projectName, String projectDesc, Integer status, Integer publicYn,
			LocalDateTime startDate, LocalDateTime endDate) {
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.status = status;
		this.publicYn = publicYn; 
		this.startDate = startDate;
		this.endDate = endDate;
	}
}