package practice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "ki_nang")
public class KiNang {
	@Id
	private String id;
	private String ten;
	private String moTa;
}
