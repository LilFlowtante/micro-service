package com.microservice.articlesservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservice.articlesservice.model.Article;

@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {
	
	Article findById(int id);
	List<Article> findByNomLike(String recherche);
	
	// Requête par convention
	List<Article> findByPrixGreaterThan(int prixLimit);
	// Requête manuelle
	@Query("SELECT p FROM Article p WHERE p.prix > :prixLimit")
	List<Article> chercherUnArticleCher(@Param("prixLimit") int prix);
	
	List<Article> findAllByOrderByNomAsc();
}
