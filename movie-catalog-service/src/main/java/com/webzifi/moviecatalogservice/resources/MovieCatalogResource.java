package com.webzifi.moviecatalogservice.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.webzifi.moviecatalogservice.models.CatalogItem;
import com.webzifi.moviecatalogservice.models.Movie;
import com.webzifi.moviecatalogservice.models.Rating;
import com.webzifi.moviecatalogservice.models.UserRating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
		
		UserRating ratings  =  restTemplate.getForObject("http://localhost:8093/ratingsdata/users/"+ userId, UserRating.class);
		
		return ratings.getUserRating().stream().map(rating -> {
			// for each movie ID, call movie info service and get details
			Movie movie = restTemplate.getForObject("http://localhost:8092/movies/"+ rating.getMovieId(), Movie.class);
//			Movie movie = webClientBuilder.build()
//				.get()
//				.uri("http://localhost:8092/movies/\"+ rating.getMovieId()")
//				.retrieve()
//				.bodyToMono(Movie.class)
//				.block();
			//put them all together
			return new CatalogItem(movie.getName(), "Desc", rating.getRating());
		})
		.collect(Collectors.toList());

		//return Collections.singletonList(o);
		
	}
}
