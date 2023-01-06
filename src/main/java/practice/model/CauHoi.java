package practice.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "cau_hoi")
public class CauHoi {
	@Id
	private String id;
	private String loai;
	private String moTa;
	@OneToMany(targetEntity = LuaChon.class)
	private List<LuaChon> luaChon = new ArrayList<LuaChon>();
}
