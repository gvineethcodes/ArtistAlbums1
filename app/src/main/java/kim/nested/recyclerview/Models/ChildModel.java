package kim.nested.recyclerview.Models;

public class ChildModel {
      private  String hero_image;
    private String movieName;

    public ChildModel(String hero_image, String movieName){
        this.hero_image = hero_image;
         this.movieName = movieName;
    }
    public String getHeroImage() {
        return hero_image;
    }
    public String getMovieName() {
        return movieName;
    }
}