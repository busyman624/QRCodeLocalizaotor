package qrcodelocalizator.qrcodelocalizator;

//model odpowiedzi serwera
public class ResponseModel {

    private int statusCode;
    private String message;
    private RoomModel room;

    ResponseModel(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getMessage(){
        return message;
    }

    public RoomModel getRoom(){
        return room;
    }

    public void setRoom(RoomModel room){
        this.room = room;
    }
}
