package models;

public class Vase extends Entity {
    private String contentType;
    private String vaseType;

    public Vase(String contentType, String vaseType) {
        this.contentType = contentType;
        this.vaseType = vaseType;
    }

    public void dropSeedPacket(){
        //TODO
    }
    public void smash(MatchState state){
        //TODO
    }



    public String getContentType() {return contentType;}
    public void setContentType(String contentType) {this.contentType = contentType;}
    public String getVaseType() {return vaseType;}
    public void setVaseType(String vaseType) {this.vaseType = vaseType;}
}
