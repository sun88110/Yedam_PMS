package com.pms.setting.groups.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pms.setting.groups.service.GroupsService;
import com.pms.setting.groups.vo.GroupsVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings/api/groups")
public class GroupsController {

	private final GroupsService groupsService;

	@GetMapping
	public List<GroupsVO> getAll() {
		return groupsService.getGroupAll();
	}

	@GetMapping("/{groupNo}")
	public GroupsVO getOne(@PathVariable Long groupNo) {
		return groupsService.getGroup(groupNo);
	}

	@GetMapping("/search")
	public List<GroupsVO> search(@RequestParam(required = false) String keyword) {

		return groupsService.search(keyword);
	}

	@PostMapping
	public String register(@RequestBody GroupsVO vo) {
		groupsService.register(vo);
		return "SUCCESS";
	}

	@PatchMapping("/{groupNo}/toggle-status")
	public void toggleStatus(@PathVariable Long groupNo) {
		groupsService.toggleGroupStatus(groupNo);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<String> updateGroup(@PathVariable("id") Long id, @RequestBody GroupsVO groupsVO) {
        groupsVO.setGroupNo(id);
        if (groupsService.modifyGroupDetail(groupsVO)) {
            return ResponseEntity.ok("SUCCESS");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAIL");
    }
}