package com.dramalog.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "dramas")
public class Drama {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "drama_id", nullable = false)
	private Integer dramaID;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "writer")
	private String writer;

	@Column(name = "genre")
	private String genre;

	@Column(name = "synopsis")
	private String synopsis;

	@Column(name = "main_actor_1")
	private String mainActor1;

	@Column(name = "main_actor_2")
	private String mainActor2;

	@Column(name = "release_date")
	private Integer releaseDate; // 4자리 년도

	@Column(name = "episode_count")
	private Integer episodeCount;

	@Column(name = "cover_image")
	private String coverImage;

	@Column(
		    name = "avg_rating",
		    precision = 3,
		    scale = 2,
		    nullable = true
		)
		private BigDecimal avgRating; // BigDecimal이 double보다 정확

	@Column(name = "hot_episode", nullable=false)
	private Integer hotEpisode = 0;

	public Drama() {}

	public Integer getDramaID() {return dramaID;}
	public String getTitle() {return title;}
	public String getWriter() {return writer;}
	public String getGenre() {return genre;}
	public String getSynopsis() {return synopsis;}
	public String getMainActor1() {return mainActor1;}
	public String getMainActor2() {return mainActor2;}
	public Integer getReleaseDate() {return releaseDate;}
	public Integer getEpisodeCount() {return episodeCount;}
	public String getCoverImage() {return coverImage;}
	public BigDecimal getAvgRating() {return avgRating;}
	public Integer getHotEpisode() {return hotEpisode;}

	public void setDramaID(Integer dramaID) {this.dramaID = dramaID;}
	public void setTitle(String title) {this.title = title;}
	public void setWriter(String writer) {this.writer = writer;}
	public void setGenre(String genre) {this.genre = genre;}
	public void setSynopsis(String synopsis) {this.synopsis = synopsis;}
	public void setMainActor1(String mainActor1) {this.mainActor1 = mainActor1;}
	public void setMainActor2(String mainActor2) {this.mainActor2 = mainActor2;}
	public void setReleaseDate(Integer releaseDate) {this.releaseDate = releaseDate;}
	public void setEpisodeCount(Integer episodeCount) {this.episodeCount = episodeCount;}
	public void setCoverImage(String coverImage) {this.coverImage = coverImage;}
	public void setAvgRating(BigDecimal avgRating) {this.avgRating = avgRating;}
	public void setHotEpisode(Integer hotEpisode) {this.hotEpisode = hotEpisode;}
}
