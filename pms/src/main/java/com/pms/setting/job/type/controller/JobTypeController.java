package com.pms.setting.job.type.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pms.setting.job.type.service.JobTypeService;
import com.pms.setting.job.type.vo.JobTypeVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings/api/job-types")
public class JobTypeController {
    private final JobTypeService jobTypeService;

    @GetMapping
    public List<JobTypeVO> list() { return jobTypeService.getList(); }

    @GetMapping("/search")
    public List<JobTypeVO> search(@RequestParam(required = false) String keyword) {
        return jobTypeService.search(keyword);
    }
    @PostMapping
    public ResponseEntity<String> add(@RequestBody JobTypeVO vo) {
        try {
            jobTypeService.register(vo);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }
    @DeleteMapping("/{no}")
    public ResponseEntity<String> remove(@PathVariable Long no) {
        try {
            jobTypeService.remove(no);
            return ResponseEntity.ok("SUCCESS");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 📍 이 부분이 있어야 프론트에서 409로 인식합니다!
            return ResponseEntity.status(409).body("REFERENCED");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}