package qrcodelocalizator.qrcodelocalizator;

public class ResponseModel {

    private int statusCode;
    private String message;

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

}
