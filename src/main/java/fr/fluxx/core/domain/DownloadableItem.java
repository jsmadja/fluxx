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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "DOWNLOADABLEITEM", uniqueConstraints = @UniqueConstraint(columnNames = { "URL" }))
@NamedQueries({ 
	@NamedQuery(name = "findDownloadableItemByUrl", query = "SELECT di FROM DownloadableItem di WHERE di.url = :url"),
	@NamedQuery(name = "deleteAllDownloadableItems", query = "DELETE FROM DownloadableItem di")
	})
public class DownloadableItem {

	public static final int MAX_DOWNLOADABLE_LINK_SIZE = 512;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = MAX_DOWNLOADABLE_LINK_SIZE, nullable = false)
	private String url;

	private String type;

	private Long fileLength;

	public DownloadableItem() {

	}

	public DownloadableItem(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getFileLength() {
		return fileLength;
	}

	public void setFileLength(Long fileLength) {
		this.fileLength = fileLength;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DownloadableItem) {
			DownloadableItem di = (DownloadableItem) obj;
			if (url == null || di.url == null) {
				return false;
			}
			return url.equals(di.url);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}
}
