package com.assignment.rest.models.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

public class DAOFacade<T> {

	private Class<T> entityClass;
	private SessionFactory sessionFactory;
	
	public DAOFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * insert data
	 * @param entity
	 * @return
	 */
    public Object create(T entity) {

        Object id = null;
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            id = session.save(entity);
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            throw re;
        } finally {
            session.close();
        }

        return id;

    }

    /**
     * update data
     * @param entity
     */
    public void edit(T entity) {

        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            
            throw re;
        } finally {
            session.close();
        }
        
    }

    /**
     * delete data
     * @param entity
     */
    public void remove(T entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            throw re;
        } finally {
            session.close();
        }
    }
    
    /**
     * select all
     * @return
     */
    public List<T> list(){
        List<T> list = null;
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            list = session.createQuery("FROM " + entityClass.getSimpleName()).list();
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            throw re;
        } finally {
            session.close();
        }
        
        return list;
    }
    
    /**
     * search by primary key
     * @param id
     * @return
     */
    public T find(Object id){
        T entityObject = null;
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            entityObject = (T)session.get(entityClass, (Serializable) id);
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            throw re;
        } finally {
            session.close();
        }
        
        return entityObject;
    }
    
    /**
     * custom HQL for returning singal row
     * @param query
     * @return
     */
    public T customQueryData(String query){
    	T entityObject = null;
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            entityObject = (T)session.createQuery(query).uniqueResult();
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            throw re;
        } finally {
            session.close();
        }
        
        return entityObject;
    }
    
    /**
     * custom HQL for returning multiple rows
     * @param query
     * @return
     */
    public List<T> customQueryDataList(String query){
        List<T> list = null;
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            list = session.createQuery(query).list();
            tx.commit();
        } catch (RuntimeException re) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (HibernateException he) {
                System.err.println("Error Rolling Back Tranction");
            }
            throw re;
        } finally {
            session.close();
        }
        
        return list;
    }
	
}
