package com.mococo.common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="comment_recommend")
@IdClass(CommentRecommendPK.class)
public class CommentRecommend implements Serializable{
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_number")
	@JsonIgnore
	private Comment comment;
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_number")
	@JsonIgnore
	private User user;
}
