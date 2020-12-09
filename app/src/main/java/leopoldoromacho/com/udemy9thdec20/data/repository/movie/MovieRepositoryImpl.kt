package leopoldoromacho.com.udemy9thdec20.data.repository.movie

import android.util.Log
import leopoldoromacho.com.udemy9thdec20.data.model.movie.Movie
import leopoldoromacho.com.udemy9thdec20.data.repository.movie.datasource.MovieCacheDataSource
import leopoldoromacho.com.udemy9thdec20.data.repository.movie.datasource.MovieLocalDataSource
import leopoldoromacho.com.udemy9thdec20.data.repository.movie.datasource.MovieRemoteDatasource
import leopoldoromacho.com.udemy9thdec20.domain.repository.MovieRepository

class MovieRepositoryImpl(
        private val movieRemoteDataSource: MovieRemoteDatasource,
        private val movieLocalDataSource: MovieLocalDataSource,
        private val movieCacheDataSource: MovieCacheDataSource
) : MovieRepository {

    override suspend fun getMovies(): List<Movie>? {
        return getMoviesFromCache()
    }

    override suspend fun updateMovies(): List<Movie>? {
        TODO("Not yet implemented")
    }

    suspend fun getMoviesFromAPI(): List<Movie> {
        lateinit var movieList: List<Movie>

        try {
            val response = movieRemoteDataSource.getMovies()
            val body = response.body()
            if (body != null) {
                movieList = body.movies
            }

        } catch (exception: Exception) {
            Log.i("MyTag", exception.message.toString())
        }

        return movieList
    }

    suspend fun getMoviesFromDB(): List<Movie> {
        lateinit var movieList: List<Movie>

        try {
            movieList = movieLocalDataSource.getMoviesFromDB()
        }
        catch (exception: Exception) {
            Log.i("MyTag", exception.message.toString())
        }

        if(movieList.size>0){
            return movieList
        }else{
            movieList=getMoviesFromAPI()
            movieLocalDataSource.saveMoviesToDB(movieList)
        }

        return movieList
    }

    suspend fun getMoviesFromCache(): List<Movie>  {

        lateinit var movieList: List<Movie>

        try {
            movieList = movieCacheDataSource.getMoviesFromCache()
        } catch (exception: Exception) {
            Log.i("MyTag", exception.message.toString())
        }

        if(movieList.size>0){
            return movieList
        }else {
            movieList = getMoviesFromDB()
            movieCacheDataSource.saveMoviesToCache(movieList)
        }

        return movieList
    }

}