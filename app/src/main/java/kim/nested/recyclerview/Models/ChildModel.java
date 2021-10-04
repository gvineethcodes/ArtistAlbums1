package kim.nested.recyclerview.Models;

public class ChildModel {
      private  String hero_image;
    private String movieName;
    private String artist;

    public ChildModel(String hero_image, String movieName, String artist){
        this.hero_image = hero_image;
         this.movieName = movieName;
         this.artist = artist;
    }
    public String getHeroImage() {
        return hero_image;
    }
    public String getMovieName() {
        return movieName;
    }

    public String getArtist() {
        return artist;
    }
}