package fr.kaddath.apps.fluxx.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import fr.kaddath.apps.fluxx.domain.DownloadableItem;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
@Interceptors({ ChronoInterceptor.class })
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
}
