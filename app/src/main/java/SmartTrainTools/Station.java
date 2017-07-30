/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;

import Utilities.StationReporter;
import commons.Config;

/**
 *
 * @author ashish
 */
public class Station implements Serializable {
        private final String code;
        private final String name;

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + this.code.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Station other = (Station) obj;
            // logic updated to support lower apis
            if (this.code == null || other.code == null) {
                return false;
            }

            if (this.name == null || other.name == null) {
                return false;
            }
            if (!this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return name + " - " + code;
        }

        public Station(String code, String name) {
            this.code = code;
            this.name = name;
            if (!Config.rc.isValidStation(code) || !Config.rc.isValidStation(name)) {
                StationReporter.report(code, name);
            }
        }
        
        public Station(String code){
            this.code=code;
            this.name = Config.rc.getStationName(code);
        }

        public String getCode() {
            return code;
        }

    public boolean isJunction() {
        return Config.indiaMap.wgraph.edgesOf(this.code).size() != 2;
    }

    }
