package com.pms.setting.projects.info.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ProjectResponseDTO {
    private Long projectNo;
    private String projectName;
    private String projectDesc;
    private String projectCode;
    private Integer status;
    private Integer publicYn;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<GroupInfoDTO> groups;

    @Getter @Setter
    public static class GroupInfoDTO {
        private Long groupNo;
        private String groupName;
        private Integer isPm;
        private String roleName;
        private Integer memberCount;
    }

    @Getter @Setter
    public static class GroupAddRequest {
        private Long groupNo;
        private Integer isPm; 
    }

    @Getter @Setter
    public static class ProjectUpdateRequest {
        private String projectName;
        private String projectDesc;
        private Integer status;
        private Integer publicYn;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 시간 포맷 주의
        private LocalDateTime startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endDate;
    }
}