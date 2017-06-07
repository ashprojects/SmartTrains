package SmartTrainTools;

/**
 * Created by root on 26/3/17.
 */

public class AvailabilityStatus {
    String status;
    String trainNo;

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    MyDate date;

    @Override
    public String toString() {
        return "AvailabilityStatus{" +
                "date=" + date +
                ", status='" + status + '\'' +
                ", trainNo='" + trainNo + '\'' +
                '}';
    }

    public AvailabilityStatus(String status, MyDate date) {
        this.date = date;
        this.status = status;
    }

    public MyDate getDate() {
        return date;
    }


    public String getStatus() {
        return status;
    }


    public boolean isAvailable(){
        return status.contains("AVAILABLE");
    }
}
