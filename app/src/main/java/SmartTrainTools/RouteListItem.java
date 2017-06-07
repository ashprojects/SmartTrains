/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;

/**
 *
 * @author ashish
 */
 public class RouteListItem implements Serializable{
        private final Station station;
        private Time arrivalTime;
        private Time departureTime;
        private Integer distanceFromSource;
        private Integer day;

    public void setDay(Integer day) {
        this.day = day;
    }

    public RouteListItem(Station station, Time arrivalTime, Time departureTime, Integer distanceFromSource, Integer day) {
            this.station = station;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
            this.distanceFromSource = distanceFromSource;
            this.day = day;
            
        }



    public Integer getDay() {
            return day;
        }

        public Station getStation() {
            return station;
        }

        public Time getArrivalTime() {
            return arrivalTime;
        }

        public Time getDepartureTime() {
            return departureTime;
        }

        public Integer getDistanceFromSource() {
            return distanceFromSource;
        }

        public void setArrivalTime(Time arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public void setDepartureTime(Time departureTime) {
            this.departureTime = departureTime;
        }

        public void setDistanceFromSource(Integer distanceFromSource) {
            this.distanceFromSource = distanceFromSource;
        }

        public RouteListItem(Station station) {
            this.station = station;
        }

        public RouteListItem(Station station, Time arrivalTime, Time departureTime, Integer distanceFromSource) {
            this.station = station;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
            this.distanceFromSource = distanceFromSource;
        }

        public RouteListItem(Station station, Integer distanceFromSource) {
            this.station = station;
            this.distanceFromSource = distanceFromSource;
        }
        
        public String toString(){
            return station.getCode()+"-"+distanceFromSource+"\t";
            
        }
    }
