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

package fr.fluxx.core.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import fr.fluxx.core.domain.DownloadableItem;
import fr.fluxx.core.interceptor.ChronoInterceptor;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors({ChronoInterceptor.class})
public class DownloadableItemService {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public DownloadableItem findByUrl(String url) {
		if (url == null || url.length() == 0) {
			throw new IllegalArgumentException("The url argument is required");
		}
		Query q = em.createNamedQuery("findDownloadableItemByUrl");
		q.setParameter("url", url);
		q.setMaxResults(1);
		List<DownloadableItem> list = q.getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public DownloadableItem store(final DownloadableItem downloadableItem) {
		DownloadableItem storedDownloadableItem;
		if (downloadableItem.getId() == null) {
			em.persist(downloadableItem);
			storedDownloadableItem = downloadableItem;
		} else {
			storedDownloadableItem = em.merge(downloadableItem);
		}
		em.flush();
		return storedDownloadableItem;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteAllDownloadableItems() {
		Query query = em.createNamedQuery("deleteAllDownloadableItems");
		query.executeUpdate();
		em.flush();
	}
}
