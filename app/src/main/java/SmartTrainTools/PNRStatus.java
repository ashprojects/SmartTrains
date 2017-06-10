package SmartTrainTools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by root on 10/6/17.
 */

public class PNRStatus {
    private String PNR;
    private boolean chartPrepared;
    private TravelClass travelClass;
    private MyDate dateOfJourney;
    private Station boardingPoint, from, to, reservationUpto;
    private String trainNo, trainName;
    private ArrayList<Passenger> passengers;

    public PNRStatus(String PNR) throws IOException, ParseException {
        this.PNR = PNR;
        Document document = Jsoup.connect("https://www.api.railrider.in/ajax_pnr_check.php")
                .data("pnr_post", "2332480054")
                .post();
        JSONParser parser = new JSONParser();
        JSONObject pnrStatus = (JSONObject) parser.parse(document.body().html());

        this.chartPrepared = !(pnrStatus.get("chart_prepared") == "N");
        this.travelClass = new TravelClass(pnrStatus.get("class1").toString());
        String dateOfJourney = pnrStatus.get("doj").toString();
        try {
            this.dateOfJourney = MyDate.parseMyDate(dateOfJourney, "DD-MM-YYYY");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            this.dateOfJourney = new MyDate(1, 1, 1970);
        }
        this.boardingPoint = parseStation((JSONObject) pnrStatus.get("boarding_point"));
        this.from = parseStation((JSONObject) pnrStatus.get("from_station"));
        this.to = parseStation((JSONObject) pnrStatus.get("to_station"));
        this.reservationUpto = parseStation((JSONObject) pnrStatus.get("reservation_upto"));
        this.trainNo = pnrStatus.get("train_num").toString();
        this.trainName = pnrStatus.get("train_name").toString();
        this.passengers = new ArrayList<>(6);
        for (Object passenger : (JSONArray) pnrStatus.get("passengers")) {
            passengers.add(this.parsePassenger((JSONObject) passenger));
        }

    }

    public String getPNR() {
        return PNR;
    }

    public boolean isChartPrepared() {
        return chartPrepared;
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public MyDate getDateOfJourney() {
        return dateOfJourney;
    }

    public Station getBoardingPoint() {
        return boardingPoint;
    }

    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public Station getReservationUpto() {
        return reservationUpto;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public String getTrainName() {
        return trainName;
    }

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }

    private Station parseStation(JSONObject station) {
        return new Station(station.get("code").toString(), station.get("name").toString());
    }

    public Passenger parsePassenger(JSONObject passenger) {
        return new Passenger(passenger.get("booking_status").toString(), passenger.get("current_status").toString());
    }

    public class Passenger {
        private String bookingStatus, currentStatus;

        public Passenger(String bookingStatus, String currentStatus) {
            this.bookingStatus = bookingStatus;
            this.currentStatus = currentStatus;
        }

        public String getCurrentStatus() {
            return currentStatus;
        }

        public String getBookingStatus() {
            return bookingStatus;
        }
    }
}
