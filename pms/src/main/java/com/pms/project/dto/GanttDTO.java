package com.pms.project.dto;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Alias("GanttDTO")
public class GanttDTO {
	
    private String id;
    private String text;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    private Double progress;
    private String parent;
    @JsonProperty("project_id")
    private String projectId;
    
    // dhtmlx 간트차트가 시작일과 종료일을 산정하면서 자동으로 계산하는부분이 있어 종료일은 duration 계산 이후 null 바꾸고 화면에 표시하기위한 종료일을 따로 마련
    private String displayEnd;
    // 화면에 표시하지 않을 값, 표시하면 안되는 값
    private String jobPriority; // 일감 우선순위
    private String workerName; // 일감 배정받은 작업자 이름
    
    // 조회 값을바탕으로 계산 한 값
    private int duration;
    private double curProgress;
    
}