package practice.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "nghe_hcode")
public class NgheHCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(targetEntity = NgheNghiep.class)
	private NgheNghiep ngheNghiep;
	@ManyToOne(targetEntity = HollandCode.class)
	private HollandCode hcode;
}
