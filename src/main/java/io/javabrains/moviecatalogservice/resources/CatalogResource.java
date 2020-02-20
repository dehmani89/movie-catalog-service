package io.javabrains.moviecatalogservice.resources;

import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // Get all related movie IDs
        //return Collections.singletonList(new CatalogItem("Transformer", "test", 4));


        //Code below retrieves ratings based on the list created of ratings
//        List<Rating> ratings = Arrays.asList(
//                new Rating("100",1),
//                new Rating("200",2),
//                new Rating("300",3),
//                new Rating("400",4),
//                new Rating("500",5)
//        );
//        return ratings.stream().map(rating -> {
//            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
//            return new CatalogItem(movie.getName(), "transformer description", rating.getRating());
//        }).collect(Collectors.toList());
//    };


        // for each movie ID, call movie info service and get details

        /**
         *  restTemplate.getForObject takes 2 parameters a url and an object
         *  URL can be the call to another webservice
         *  object can be the expected object served back by the service
         */

        /**
         *  retrieve rating data from the API call  "ratings-data-service"
         *  Then save the data into an object UserRating
        */
        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);

        /**
         *  Go through each rating from the previous API call "ratings-data-service" response
         *  Then retrieve movie data from the API call  "movie-info-service"
         *  Then aggregate the data into a movie CatalogItem (by combining data from "movie-info-service" and "ratings-data-service")
         *  Then save the data into an object CatalogItem
         *  Return CatalogItem object to the user
         */
        return userRating.getRatings().stream().map(rating -> {

            // This is using restTemplate to call another service and bond it to a class (as of 2020 this may become deprecated)
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            // This is using webClientBuilder to call another service and bond it to a class (as of 2020 this may become the new way to make service to service calls)
            /* Movie movie = webClientBuilder.build()
                     .get()
                     .uri("http://localhost:8082/movies/"+ rating.getMovieId())
                     .retrieve()
                     .bodyToMono(Movie.class)
                     .block();
             */

                    //The return statement below is aggregating data from 2 API calls, movie-info-service and ratings-data-service
                    return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
                }).collect(Collectors.toList());

    }




}

/*
Alternative WebClient way
Movie movie = webClientBuilder.build().get().uri("http://localhost:8082/movies/"+ rating.getMovieId())
.retrieve().bodyToMono(Movie.class).block();
*/