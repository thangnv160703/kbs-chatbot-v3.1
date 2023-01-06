package practice.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "nghe_nghiep")
public class NgheNghiep {
	@Id
	private String id;
	private String ten;
	@ManyToOne(targetEntity = BangCap.class)
	private BangCap bangCap;
	@ManyToMany(targetEntity = KiNang.class)
	private List<KiNang> kiNang;
	@ManyToMany(targetEntity = KienThuc.class)
	private List<KienThuc> kienThuc;
}
