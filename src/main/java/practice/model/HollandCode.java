package practice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "holland_code")
public class HollandCode {
	@Id
	private String id;
}
