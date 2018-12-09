package qrcodelocalizator.qrcodelocalizator;

//model pokoju tworzony po uzupelnieniu danych i wcisnieciu przycisku wyslij lub gdy pobieramy
// informacje z bazy danych na temat konkretnego numeru pokoju
public class RoomModel {

    private String roomNumber;
    private String type;
    private String availability;
    private String description;

    RoomModel(String roomNumber, String type, String availability, String description){
        this.roomNumber = roomNumber;
        this.type = type;
        this.availability = availability;
        this.description = description;
    }

    public String getRoomNumber(){
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber){
        this.roomNumber = roomNumber;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getAvailability(){
        return availability;
    }

    public void setAvailability(String availability){
        this.availability = availability;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
