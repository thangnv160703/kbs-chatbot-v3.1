package practice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "bang_cap")
public class BangCap {
	@Id
	private String id;
	private String ten;
	private int xepLoai;
}
