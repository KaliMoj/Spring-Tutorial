package tasks.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="task_id")
	private Long id;
	private String description;
	private boolean isActive;
	@ManyToOne
	@JoinColumn(name="user_id")
	User associatedUser;
	
	public Task() {
		isActive = true;
	}

	public String getDescription() {
		return description;
	}

	@JsonIgnore
	public User getUser() {
		return associatedUser;
	}

	public void setUser(User user) {
		this.associatedUser = user;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
