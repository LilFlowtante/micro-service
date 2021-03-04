package com.microservice.articlesservice.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.microservice.articlesservice.dao.ArticleDao;
import com.microservice.articlesservice.model.Article;
import com.microservice.articlesservice.web.exceptions.ArticleIntrouvableException;
import com.microservice.articlesservice.web.exceptions.ArticlePrixEgalZeroException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(description = "API pour les opérations CRUD sur les articles.")
public class ArticleController {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	
	@Autowired
	private HttpServletRequest requestContext;
	
	@Autowired
	private ArticleDao articleDao;
	
	@ApiOperation(value = "Récupère la liste de tous les articles.")
	@GetMapping(value = "/Articles")
	public MappingJacksonValue listeArticles() {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		List<Article> articles = articleDao.findAll();
		
		SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
		
		FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
		
		MappingJacksonValue articlesFiltres = new MappingJacksonValue(articles);
		
		articlesFiltres.setFilters(listDeNosFiltres);
		
		return articlesFiltres;
	}
	
	@ApiOperation(value = "Récupère un article grâce à son ID à condition que celui-ci soit en stock!")
	@GetMapping(value = "/Articles/{id}")
	public Article afficherUnArticle(@PathVariable int id) {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		Article article = articleDao.findById(id);
		if(article == null) {
			throw new ArticleIntrouvableException("L'article avec l'id " + id + " est INTROUVABLE.");
		}
		return article;
	}
	
	@ApiOperation(value = "Calcule la marge de chaque article (différence entre prix d'achat et prix de vente).")
	@GetMapping(value = "/AdminArticles")
	public List<String> calculerMargeArticle() {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		List<Article> articles = articleDao.findAll();
		List<String> resultArray = new ArrayList<>();
		for(Article article: articles) {
			int margeArticle = article.getPrix() - article.getPrixAchat();
			String result = article.getNom() + " / marge de: " + margeArticle + "€;";
			resultArray.add(result);
		}
		return resultArray;
	}
	
	@ApiOperation(value = "Retourne la liste de tous les articles triés par nom croissant.")
	@GetMapping(value = "/Articles/order")
	public List<Article> trierArticlesParOrdreAlphabetique() {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		return articleDao.findAllByOrderByNomAsc();
	}
	
	/* TEST MAPPING - DEBUT */
	@GetMapping(value = "/test/articles/{prixLimit}")
	public List<Article> testePrixLimite(@PathVariable int prixLimit) {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		return articleDao.findByPrixGreaterThan(prixLimit);
	}
	
	@GetMapping(value = "/test/articles/like/{recherche}")
	public List<Article> testeLike(@PathVariable String recherche) {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		return articleDao.findByNomLike("%"+recherche+"%");
	}
	/* TEST MAPPING - FIN */
	
	@ApiOperation(value = "Ajoute un nouvel article.")
	@PostMapping(value = "/Articles")
	public ResponseEntity<Void> ajouterArticle(@RequestBody Article article) {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		if(article.getPrix() == 0) {
			throw new ArticlePrixEgalZeroException("Impossible d'enregistrer un article dont le prix est 0.");
		}
		
		Article articleAdded = articleDao.save(article);
		
		if(articleAdded == null) {
			return ResponseEntity.noContent().build();
		}
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(articleAdded.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@ApiOperation(value = "Supprime l'article par son ID.")
	@DeleteMapping(value = "/Articles/{id}")
	public void supprimerArticle(@PathVariable int id) {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		articleDao.deleteById(id);
	}
	
	@ApiOperation(value = "Met à jour l'article.")
	@PutMapping(value = "/Articles")
	public void updateArticle(@RequestBody Article article) {
		logger.info("Début d'appel au service Articles pour la requête: " + requestContext.getHeader("req-id"));
		
		articleDao.save(article);
	}
}
