package com.mococo.common.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class MaterialPK implements Serializable{
	private int recipeNumber;
	private int cropNumber;
}
