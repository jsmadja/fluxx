/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.fluxx.core.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;

@Entity
@NamedQueries({
	@NamedQuery(name="getNumCustomFeeds", query="SELECT COUNT(customFeed) FROM CustomFeed customFeed"),
	@NamedQuery(name="deleteAllCustomFeeds", query= "DELETE FROM CustomFeed cf")
})
public class CustomFeed implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToMany
	@JoinTable(name = "CUSTOMFEED_FEED", joinColumns = { @JoinColumn(name = "CUSTOMFEED_ID", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "FEED_ID", referencedColumnName = "ID") })
	@OrderBy("title ASC")
	private List<Feed> feeds;

	@Basic(optional=false)
	private String username;

	@Column(nullable=false)
	private String category;

	@Temporal(javax.persistence.TemporalType.DATE)
	private Date referentDay;

	private Integer numLastDay;

	public Integer getNumLastDay() {
		return numLastDay;
	}

	public void setNumLastDay(Integer numLastDay) {
		this.numLastDay = numLastDay;
	}

	public Date getReferentDay() {
		return referentDay;
	}

	public void setReferentDay(Date referentDay) {
		this.referentDay = referentDay;
	}

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CustomFeed) {
			CustomFeed cf = (CustomFeed) obj;
			if (username == null || cf.username == null || category == null || cf.category == null) {
				return false;
			}
			return username.equals(cf.username) && category.equals(cf.category);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return category.hashCode() + username.hashCode();
	}

	@Override
	public String toString() {
		return category;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
