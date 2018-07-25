/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruta.turismo.rutaturismo.session;

import com.ruta.turismo.rutaturismo.Ruta;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.eclipse.persistence.config.QueryHints;

/**
 *
 * @author cuer
 */
@Stateless
public class RutaFacade extends AbstractFacade<Ruta> {

    @PersistenceContext(unitName = "com.ruta.turismo_RutaTurismo_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RutaFacade() {
        super(Ruta.class);
    }
      public Integer incremento()
    {
    
      String jpqlQuery = "SELECT LAST_INSERT_ID()";
      Query query = this.em.createNativeQuery(jpqlQuery);
      Integer autoincrementoMysql = Integer.parseInt(String.valueOf(query.getSingleResult()));
      return autoincrementoMysql;
    
    }
    public List<Ruta> findRustasActivas() {
           Query query = getEntityManager().createQuery("select c from Ruta c where c.estado = 'A'");
//	    query.setHint("javax.persistence.cache.storeMode", "REFRESH");	
            query.setHint(QueryHints.REFRESH, true);
           try {
                    
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
			// TODO: handle exception
		}

    }

     @Override
    public Ruta find(Object id) {
         Query query = getEntityManager().createQuery("select c from Ruta c where c.idRuta =:id");
         query.setParameter("id", id);
	    query.setHint(QueryHints.REFRESH, true);	
           try {
                    
		return (Ruta)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
			// TODO: handle exception
		}
    }
    
    
}
