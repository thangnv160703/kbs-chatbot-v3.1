package practice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import practice.model.HollandCode;
import practice.model.KiNang;
import practice.model.KienThuc;
import practice.model.NgheHCode;
import practice.model.NgheNghiep;
import practice.repository.HollandCodeRepository;
import practice.repository.NgheHCodeRepository;

@Service
@Slf4j
public class CareerService {
	@Autowired
	private NgheHCodeRepository ngheHCodeRepo;
	
	@Autowired
	private HollandCodeRepository hollandCodeRepo;
	
	public ArrayList<NgheNghiep> findBestJobs(String body, ArrayList<NgheNghiep> job_list, String hcode){
		JsonParser springParser = JsonParserFactory.getJsonParser();
		Map<String, Object> map;
//		log.info(body);
		map = springParser.parseMap(body);
		int edu_lvl = (int) map.get("edu_lvl");
		int willing_edu_lvl = (int) map.get("willing_edu_lvl");
		ArrayList<String> fav_knowledge = (ArrayList<String>) map.get("fav_knowledge");
		ArrayList<String> learned_knowledge = (ArrayList<String>) map.get("learned_knowledge");
		ArrayList<String> used_skill = (ArrayList<String>) map.get("used_skill");
		ArrayList<String> used_knowledge = (ArrayList<String>) map.get("used_knowledge");
		
		
		ArrayList<CompJob> compjob_list = new ArrayList<>();
		ArrayList<FavJob> favjob_list = new ArrayList<>();
		for(NgheNghiep job: job_list) {
			int comp = 0, fav = 0;
			if(job.getBangCap().getXepLoai() <= edu_lvl) {
				comp += 1;
			}
			if(job.getBangCap().getXepLoai() <= willing_edu_lvl) {
				comp += 1;
			}
			for(KienThuc kt: job.getKienThuc()) {
				String kt_id = kt.getId();
				if(fav_knowledge.contains(kt_id)) {
					fav += 1;
					comp += 1;
				}
				if(learned_knowledge.contains(kt_id)) {
					comp += 1;
				}
				if(used_knowledge.contains(kt_id)) {
					comp += 1;
				}
			}
			for(KiNang kn: job.getKiNang()) {
				String kn_id = kn.getId();
				if(used_skill.contains(kn_id)) {
					comp += 1;
				}
			}
			compjob_list.add(new CompJob(job, comp));
			favjob_list.add(new FavJob(job, fav));
		}
		
		Collections.sort(compjob_list);
		for(CompJob cj: compjob_list) {
			log.info(cj.getNghe().getId() + " " + cj.getNghe().getTen());
			log.info("" + cj.getComp());
		}
		
		Collections.sort(favjob_list);
		
		if((favjob_list.size() > 0) && hcode!="") {
			if(favjob_list.get(0).getFav() >= 2) {
				updateNgheHCode(favjob_list.get(0).getNghe(), hcode);
			}
		}
		
		ArrayList<NgheNghiep> advised_jobs = new ArrayList<NgheNghiep>();
		for(int i=0; i<10 && i<compjob_list.size(); ++i) {
			advised_jobs.add(compjob_list.get(i).getNghe());
		}
		
		return advised_jobs;
	}

	public ArrayList<String> findHollandCode(String body) {
//		Tach chuoi json
		String[] json_array = body.substring(1, body.length() - 2).split("},");
		JsonParser springParser = JsonParserFactory.getJsonParser();
		Map<String, Object> map;
		Map<String, Integer> chiSo_list = new HashMap<>();

		for (int i = 0; i < json_array.length; ++i) {
			map = springParser.parseMap(json_array[i] + "}");
			chiSo_list.put(map.get("name").toString(), Integer.parseInt(map.get("kq").toString()));

		}

//		lay nhung chi so khac 0
		ArrayList<Result> result_list = new ArrayList<>();
		for (String chiSo : chiSo_list.keySet()) {
			if (chiSo_list.get(chiSo) != 0) {
				result_list.add(new Result(chiSo, chiSo_list.get(chiSo)));
			}
		}
		
		if(result_list.size() == 0) {
			result_list.add(new Result("R", 100));
			result_list.add(new Result("I", 100));
			result_list.add(new Result("A", 100));
			result_list.add(new Result("S", 100));
			result_list.add(new Result("E", 100));
			result_list.add(new Result("C", 100));
		}
		
//		sap xep
		Collections.sort(result_list);

		ArrayList<String> code_list = getHollandCode(result_list);
		log.info("Ket qua: " + code_list.toString());
		log.info("So code: " + code_list.size());
		
		return code_list;
	}
	
	public HashSet<NgheNghiep> findJob(ArrayList<String> code_list){
		ArrayList<HollandCode> all_hollandCode = (ArrayList<HollandCode>) hollandCodeRepo.findAll();
		HashSet<HollandCode> hollandCode_set = new HashSet<>();
		for(String hcode1: code_list) {
			ArrayList<HollandCode> most_suitable = findMostIdenticalHCodes(hcode1, all_hollandCode);
			hollandCode_set.addAll(most_suitable);
		}
		
		log.info("Holland Code set: " + hollandCode_set.toString());

		HashSet<NgheNghiep> job_set = new HashSet<>();
		for (HollandCode code : hollandCode_set) {
			List<NgheHCode> job_hcode = ngheHCodeRepo.findByHcode(code);
			for(NgheHCode nhc: job_hcode) {
				job_set.add(nhc.getNgheNghiep());
			}
		}

		return job_set;
	}

	private void updateNgheHCode(NgheNghiep nghe, String hcode) {
		HollandCode code = hollandCodeRepo.findById(hcode).orElse(null);
		if(code == null) {
			code = new HollandCode();
			code.setId(hcode);
			hollandCodeRepo.save(code);
		}
		ArrayList<NgheHCode> ngheHCode_list = (ArrayList<NgheHCode>) ngheHCodeRepo.findByHcode(code);
		
		if(ngheHCode_list.size() >= 5) {
			return;
		}
		else {
			for(NgheHCode nc: ngheHCode_list) {
				if(nc.getNgheNghiep().getId().equals(nghe.getId())) {
					return;
				}
			}
		}
		
		NgheHCode nhc = new NgheHCode();
		nhc.setNgheNghiep(nghe);
		nhc.setHcode(code);
		
		ngheHCodeRepo.save(nhc);
	}
	
	private ArrayList<HollandCode> findMostIdenticalHCodes(String hcode1, ArrayList<HollandCode> code_list){
		ArrayList<SuitCode> suitcode_list = new ArrayList<>();
		for(HollandCode code: code_list) {
			String hcode2 = code.getId();
			float d1 = 0;
			float x = 0;
			for(int i=0; i<hcode1.length(); ++i) {
				if(hcode2.indexOf(hcode1.charAt(i)) != -1) {
					x += 1;
				}
			}
			d1 += x / (float) Math.max(hcode1.length(), hcode2.length());
			float score = d1*8;
			
			HashMap<String, Integer> map = new HashMap<>();
			map.put("R", 0);
			map.put("I", 1);
			map.put("A", 2);
			map.put("S", 3);
			map.put("E", 4);
			map.put("C", 5);
			float[][] matrix = { 
					{ 1, (float) 0.75, (float) 0.25, 0, (float) 0.25, (float) 0.75 }, 
					{ (float) 0.75, 1, (float) 0.75, (float) 0.25, 0, (float) 0.25 }, 
					{ (float) 0.25, (float) 0.75, 1, (float) 0.75, (float) 0.25, 0 }, 
					{ 0, (float) 0.25, (float) 0.75, 1, (float) 0.75, (float) 0.25 },
					{ (float) 0.25, 0, (float) 0.25, (float) 0.75, 1, (float) 0.75 }, 
					{ (float) 0.75, (float) 0.25, 0, (float) 0.25, (float) 0.75, 1 }, 
			};
			
			for(int i=0; i<3; ++i) {
				if(hcode1.length() < (i+1)) {
					if(hcode2.length() < (i+1)) {
						score += 1 * (3-i);
					}
					else score += 0.25 + (3-i);
				}
				else {
					if(hcode2.length() < (i+1)) {
						score += 0.25 * (3-i);
					}
					else{
						String s1 = hcode1.substring(i, i+1);
						String s2 = hcode2.substring(i, i+1);
						score += matrix[map.get(s1)][map.get(s2)] * (3-i);
					}
				}
			}
			
			score = score / 14;
			
			suitcode_list.add(new SuitCode(code, score));
		}
		
		Collections.sort(suitcode_list);
		ArrayList<HollandCode> hollandCode_list = new ArrayList<>();
		for(int i=0; i<3; ++i) {
			hollandCode_list.add(suitcode_list.get(i).getCode());
		}
		
		return hollandCode_list;
	}
	
	private ArrayList<String> getHollandCode(ArrayList<Result> result_list) {
		ArrayList<String> hollandCode_list = new ArrayList<>();
		if (result_list.isEmpty()) {

		} else if (result_list.size() == 1) {
			hollandCode_list.add(result_list.get(0).getId());
		} else if (result_list.size() == 2) {
			if (result_list.get(0).getScore() == result_list.get(1).getScore()) {
				hollandCode_list.add(result_list.get(0).getId() + result_list.get(1).getId());
				hollandCode_list.add(result_list.get(1).getId() + result_list.get(0).getId());
			} else {
				hollandCode_list.add(result_list.get(0).getId() + result_list.get(1).getId());
			}
		} else {
			int loc = 3;
			while (loc < result_list.size() && (result_list.get(2).getScore() == result_list.get(loc).getScore())) {
				loc += 1;
			}
			if (result_list.get(0).getScore() == result_list.get(1).getScore()) {
				if (result_list.get(1).getScore() == result_list.get(2).getScore()) {
					String str = "";
					for (int i = 0; i < loc; ++i) {
						str += result_list.get(i).getId();
					}
					ArrayList<String> dsChinhHop = new ArrayList<>();
					getChinhHop(str, "", 3, dsChinhHop);
					for (String x : dsChinhHop) {
						hollandCode_list.add(x);
					}
				} else {
					String str1 = result_list.get(0).getId() + result_list.get(1).getId();
					String str2 = result_list.get(1).getId() + result_list.get(0).getId();
					for (int i = 2; i < loc; ++i) {
						hollandCode_list.add(str1 + result_list.get(i).getId());
						hollandCode_list.add(str2 + result_list.get(i).getId());
					}
				}
			} else {
				if (result_list.get(1).getScore() == result_list.get(2).getScore()) {
					String str = "";
					for (int i = 1; i < loc; ++i) {
						str += result_list.get(i).getId();
					}
					ArrayList<String> dsChinhHop = new ArrayList<>();
					getChinhHop(str, "", 2, dsChinhHop);
					for (String x : dsChinhHop) {
						hollandCode_list.add(result_list.get(0).getId() + x);
					}
				} else {
					String str1 = result_list.get(0).getId() + result_list.get(1).getId();
					for (int i = 2; i < loc; ++i) {
						hollandCode_list.add(str1 + result_list.get(i).getId());
					}
				}
			}
		}

		if (hollandCode_list.size() <= 3) {
			return hollandCode_list;
		} else {
			return getUuTien(hollandCode_list);
		}
	}

	private ArrayList<String> getUuTien(ArrayList<String> result_list) {
		HashMap<String, Integer> map = new HashMap<>();
		map.put("R", 0);
		map.put("I", 1);
		map.put("A", 2);
		map.put("S", 3);
		map.put("E", 4);
		map.put("C", 5);
		
		int[][] matrix = { { 0, 1, 0, -1, 0, 1 }, { 1, 0, 1, 0, -1, 0 }, { 0, 1, 0, 1, 0, -1 }, { -1, 0, 1, 0, 1, 0 },
				{ 0, -1, 0, 1, 0, 1 }, { 1, 0, -1, 0, 1, 0 }, };

		int max_value = -10;
		HashMap<String, Integer> priority = new HashMap<>();
		for (String result : result_list) {
			int first = (int) map.get(result.substring(0, 1));
			int second = (int) map.get(result.substring(1, 2));
			int third = (int) map.get(result.substring(2));
			int value = 3 * matrix[first][second] + 2 * matrix[first][third] + matrix[second][third];
//			log.info(result + ":" + value);
			if (max_value < value) {
				max_value = value;
				priority.clear();
				priority.put(result, value);
			} else if (max_value == value) {
				priority.put(result, value);
			}
		}
		ArrayList<String> final_codes = new ArrayList<>();

		for (String s : priority.keySet()) {
			final_codes.add(s);
		}
		return final_codes;
	}
	
	private void getChinhHop(String a, String sub, int len, ArrayList<String> list) {
        if(sub.length() == len){
            list.add(sub);
        }
        for (int i = 0; i < a.length(); ++i) {
            if(sub.indexOf(a.charAt(i)) == -1){
                getChinhHop(a, sub + a.charAt(i), len, list);
            }
        }
    }
	
	@Data
	private class SuitCode implements Comparable<SuitCode>{
		private HollandCode code;
		private float score;
		
		public SuitCode(HollandCode code, float score) {
			this.code = code;
			this.score = score;
		}
		
		@Override
		public int compareTo(SuitCode o) {
			if(o.score > this.score) {
				return 1;
			}
			else return -1;
		}
	}
	
	@Data
	private class Result implements Comparable<Result>{
		private String id;
	    private int score;
	    
	    public Result(String id, int score) {
	    	this.id = id; 
	    	this.score = score;
	    }
	    
		@Override
		public int compareTo(Result o) {
			return o.score - this.score;
		}
	}
	
	@Data
	private class CompJob implements Comparable<CompJob>{
		private NgheNghiep nghe;
		private int comp;
		
		public CompJob(NgheNghiep nghe, int comp) {
			this.nghe = nghe;
			this.comp = comp;
		}
		
		@Override
		public int compareTo(CompJob o) {
			return o.comp - this.comp;
		}
	}
	
	@Data
	private class FavJob implements Comparable<FavJob>{
		private NgheNghiep nghe;
		private int fav;
		
		public FavJob(NgheNghiep nghe, int fav) {
			this.nghe = nghe;
			this.fav = fav;
		}

		@Override
		public int compareTo(FavJob o) {
			return o.fav - this.fav;
		}
	}
}
