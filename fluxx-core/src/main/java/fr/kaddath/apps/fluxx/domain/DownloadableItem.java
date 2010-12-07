package fr.kaddath.apps.fluxx.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "DOWNLOADABLEITEM", uniqueConstraints = @UniqueConstraint(columnNames = { "URL" }))
public class DownloadableItem {

	public static final int MAX_DOWNLOADABLE_LINK_SIZE = 512;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(length = MAX_DOWNLOADABLE_LINK_SIZE)
	private String url;

	private String type;

	private Long fileLength;

	public DownloadableItem() {

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
		if (obj == null || !(obj instanceof DownloadableItem)) {
			return false;
		}
		DownloadableItem di = (DownloadableItem) obj;
		return url.equals(di.url);
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}
}
