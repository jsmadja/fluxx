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

package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
@Interceptors({ChronoInterceptor.class})
public class DownloadableItemService {

    @PersistenceContext
    EntityManager em;

    @SuppressWarnings("unchecked")
    public DownloadableItem findByUrl(String url) {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("The url argument is required");
        }
        Query q = em.createQuery("SELECT d FROM DownloadableItem d WHERE d.url = :url");
        q.setParameter("url", url);
        q.setMaxResults(1);
        List<DownloadableItem> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public DownloadableItem store(DownloadableItem downloadableItem) {
        if (downloadableItem.getId() == null) {
            em.persist(downloadableItem);
        } else {
            downloadableItem = em.merge(downloadableItem);
        }
        em.flush();
        return downloadableItem;
    }
}
