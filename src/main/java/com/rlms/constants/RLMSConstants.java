package com.rlms.constants;

public enum RLMSConstants {

	ACTIVE(1,"Active"),
	INACTIVE(0,"In Active"),
	LIFT_TYPE(7,"Lift Type"),
	INDITECH(1,"Inditech"),
	COMPLAINT_REG_TYPE_END_USER(30, "Registered by Customer"),
	COMPLAINT_REG_TYPE_ADMIN(31, "Registered over Phone"),
	COMPLAINT_REG_TYPE_LIFT_EVENT(32, "Generated by Lift events"),
	USER_ROLE_TYPE(33,"user_role_type"),
	MEMBER_TYPE(34,"Member Type"),
	MINUS_ONE(-1, "Minus One"),
	NA(10, "NA"),
	ZERO(0,"Zero");
	
	private Integer id;
	
	private String name;
	
	RLMSConstants(Integer id, String name){
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
