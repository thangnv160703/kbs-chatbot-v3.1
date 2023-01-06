package practice.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import practice.model.BangCap;
import practice.model.HollandCode;
import practice.model.KiNang;
import practice.model.KienThuc;
import practice.model.NgheNghiep;
import practice.service.CareerService;

@RestController
@RequestMapping("/answer")
@Slf4j
public class AnswerController {
	@Autowired
	CareerService careerService;

	@PostMapping
	public DieuKien getCareer(@RequestBody String body, HttpSession session) {
		log.info("Received");
//		HashSet<NgheNghiep> job_set = careerService.findHollandCode(body);
		ArrayList<String> code_list = careerService.findHollandCode(body);
		if(code_list.size() == 1) {
			session.setAttribute("hcode", code_list.get(0));
		}
		else {
			session.setAttribute("hcode", "");
		}
		HashSet<NgheNghiep> job_set = careerService.findJob(code_list);
		
		log.info("Number of jobs: " + job_set.size());
		
		List<NgheNghiep> session_job = new ArrayList<NgheNghiep>(job_set);
		session.setAttribute("job_list", session_job);
		
		HashSet<KiNang> ki_nang_set = new HashSet<>();
		HashSet<KienThuc> kien_thuc_set = new HashSet<>();
		
		for(NgheNghiep nghe: job_set) {
			ki_nang_set.addAll(nghe.getKiNang());
			kien_thuc_set.addAll(nghe.getKienThuc());
		}
		
		DieuKien dieuKien = new DieuKien();
		dieuKien.setKienThuc(new ArrayList<KienThuc>(kien_thuc_set));
		dieuKien.setKiNang(new ArrayList<KiNang>(ki_nang_set));
		
		return dieuKien;
	}
	
	@PostMapping("/further-info")
	public ArrayList<NgheNghiep> findSuitableJobs(@RequestBody String body, HttpSession session){
		log.info("Find best jobs.");
		ArrayList<NgheNghiep> job_list;
		String hcode;
		
		try {
			job_list = (ArrayList<NgheNghiep>) session.getAttribute("job_list");
		} catch (Exception e) {
			job_list = new ArrayList<>();
		}
		try {
			hcode = (String) session.getAttribute("hcode");
		} catch (Exception e) {
			hcode = "";
		}
		ArrayList<NgheNghiep> best_job_list = careerService.findBestJobs(body, job_list, hcode);
		return best_job_list;
	}
	
//	// testing
//	@PostMapping("/job-session")
//	public List<NgheNghiep> getSessionJobs(HttpSession session){
//		List<NgheNghiep> job_list;
//		try {
//			job_list = (List<NgheNghiep>) session.getAttribute("job_list");
//		} catch (Exception e) {
//			job_list = new ArrayList<NgheNghiep>();
//		}
//		return job_list;
//	}
//	
//	//testing
//	@PostMapping("/hcode-session")
//	public String getHollandCode(HttpSession session) {
//		log.info("Received");
//		String hcode;
//		try {
//			 hcode = (String) session.getAttribute("hcode");
//		} catch (Exception e) {
//			hcode = "";
//		}
//		log.info(hcode.toString());
//		return hcode;
//	}
	
	@Data
	private class DieuKien{
		private ArrayList<KiNang> kiNang;
		private ArrayList<KienThuc> kienThuc;

	}
	
}
