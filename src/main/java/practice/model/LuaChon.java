package practice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "lua_chon")
public class LuaChon {
	@Id
	private String id;
	private String moTa;
	private int chon;
	private int khongChon;
}
