package com.pms.setting.job.status.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pms.setting.job.status.service.JobStatusService;
import com.pms.setting.job.status.vo.JobStatusVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings/api/job-status")
public class JobStatusController {
    private final JobStatusService jobStatusService;

    @GetMapping
    public List<JobStatusVO> list() { return jobStatusService.getList(); }

    @GetMapping("/search")
    public List<JobStatusVO> search(@RequestParam(required = false) String keyword) {
        return jobStatusService.search(keyword);
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody JobStatusVO vo) {
        jobStatusService.register(vo);
        return ResponseEntity.ok("SUCCESS");
    }
    
    @DeleteMapping("/{no}")
    public ResponseEntity<String> remove(@PathVariable Long no) {
        try {
            jobStatusService.remove(no);
            return ResponseEntity.ok("SUCCESS");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 📍 외래 키(FK) 참조 오류가 발생했을 때 Catch!
            return ResponseEntity.status(409).body("REFERENCED"); 
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR");
        }
    }
}
