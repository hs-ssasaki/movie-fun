package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    TransactionOperations moviesTransactionOperations;
    TransactionOperations albumsTransactionOperations;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures
                            , TransactionOperations moviesTransactionOperations, TransactionOperations albumsTransactionOperations) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.moviesTransactionOperations = moviesTransactionOperations;
        this.albumsTransactionOperations = albumsTransactionOperations;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        //transactionCallbackWithoutResult = new TransactionCallbackWithoutResult() ;

        moviesTransactionOperations.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                for (Movie movie : movieFixtures.load()) {
                    moviesBean.addMovie(movie);

                }
                return null;
            }
//            @Override
//                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
//                    for (Movie movie : movieFixtures.load()) {
//                        moviesBean.addMovie(movie);
//
//                    }
//                }
//            }
        });

        albumsTransactionOperations.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                for (Album album : albumFixtures.load()) {
                    albumsBean.addAlbum(album);
                }
                return null;
            }

//                                                @Override
//            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
//                for (Album album : albumFixtures.load()) {
//                    albumsBean.addAlbum(album);
//                }
//
//            }
        });

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
