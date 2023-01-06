package practice.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import practice.model.HollandCode;
import practice.model.NgheHCode;
import practice.model.NgheNghiep;

public interface NgheHCodeRepository extends CrudRepository<NgheHCode, Long>{
	
	List<NgheHCode> findByHcode(HollandCode hcode);
}
