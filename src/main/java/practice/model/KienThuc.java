package practice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "kien_thuc")
public class KienThuc {
	@Id
	private String id;
	private String linhVuc;
	private String moTa;
}
